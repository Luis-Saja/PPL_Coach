package com.ukdw.pplaicoach.presentation.tracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ukdw.pplaicoach.PPLAICoachApp
import com.ukdw.pplaicoach.data.local.*
import com.ukdw.pplaicoach.data.repository.TrackerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * === TRACKER VIEW MODEL ===
 *
 * ViewModel untuk fitur Workout Tracker.
 * Mengelola state untuk:
 * - Auto-suggest hari PPL (Push→Pull→Legs→Push)
 * - Daftar exercise untuk hari yang dipilih
 * - Sesi latihan aktif dan set records
 * - Rest timer countdown
 */
class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrackerRepository =
        (application as PPLAICoachApp).trackerRepository

    // ========================
    // AUTO-SUGGEST HARI PPL
    // Logika: query sesi terakhir → suggest hari berikutnya dalam rotasi
    // ========================

    /** Hari yang disarankan oleh sistem (berdasarkan sesi terakhir) */
    val suggestedDay: StateFlow<ExerciseCategory> = repository
        .getSuggestedDay()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExerciseCategory.PUSH
        )

    /** Hari yang sedang dipilih pengguna (bisa di-override dari suggestion) */
    private val _selectedDay = MutableStateFlow(ExerciseCategory.PUSH)
    val selectedDay: StateFlow<ExerciseCategory> = _selectedDay.asStateFlow()

    /** Daftar exercise untuk hari yang dipilih */
    val exercises: StateFlow<List<ExerciseEntity>> = _selectedDay
        .flatMapLatest { category ->
            repository.getExercisesByCategory(category)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ========================
    // SESI LATIHAN AKTIF
    // ========================

    /** ID sesi latihan yang sedang aktif (null = belum mulai) */
    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    /** Set records yang sudah dicatat di sesi aktif */
    val setRecords: StateFlow<List<SetRecordEntity>> = _currentSessionId
        .flatMapLatest { sessionId ->
            if (sessionId != null) {
                repository.getRecordsBySession(sessionId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Apakah ada sesi yang sedang aktif */
    val isWorkoutActive: StateFlow<Boolean> = _currentSessionId
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    // ========================
    // REST TIMER
    // Countdown timer menggunakan coroutine delay
    // ========================

    private val _restTimerSeconds = MutableStateFlow(0)
    val restTimerSeconds: StateFlow<Int> = _restTimerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    /** Total durasi timer yang dipilih (untuk menghitung progress) */
    private val _totalTimerDuration = MutableStateFlow(0)
    val totalTimerDuration: StateFlow<Int> = _totalTimerDuration.asStateFlow()

    private var timerJob: Job? = null

    // ========================
    // ACTIONS
    // ========================

    /** Pilih hari latihan (override auto-suggest) */
    fun selectDay(category: ExerciseCategory) {
        _selectedDay.value = category
    }

    /** Sinkronkan selectedDay dengan suggestedDay saat pertama kali */
    init {
        viewModelScope.launch {
            suggestedDay.collect { suggested ->
                if (_currentSessionId.value == null) {
                    _selectedDay.value = suggested
                }
            }
        }
    }

    /**
     * Mulai sesi latihan baru.
     * Membuat record TrackerSession di database dan menyimpan ID-nya.
     */
    fun startWorkout() {
        viewModelScope.launch {
            val sessionId = repository.startSession(_selectedDay.value)
            _currentSessionId.value = sessionId
        }
    }

    /**
     * Catat satu set latihan.
     * Setelah set dicatat, rest timer otomatis dimulai (90 detik default).
     */
    fun logSet(
        exerciseId: Int,
        weightInKg: Double,
        reps: Int,
        intensityType: IntensityType = IntensityType.NORMAL
    ) {
        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            repository.logSet(exerciseId, sessionId, weightInKg, reps, intensityType)
            // Otomatis mulai rest timer setelah log set
            startRestTimer(90)
        }
    }

    /** Hapus satu set record */
    fun deleteSetRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteSetRecord(id)
        }
    }

    /**
     * Mulai countdown rest timer.
     * Menggunakan coroutine delay untuk countdown setiap detik.
     *
     * @param durationSeconds durasi timer dalam detik (60, 90, atau 120)
     */
    fun startRestTimer(durationSeconds: Int) {
        // Cancel timer yang sedang jalan (jika ada)
        timerJob?.cancel()
        _totalTimerDuration.value = durationSeconds
        _restTimerSeconds.value = durationSeconds
        _isTimerRunning.value = true

        timerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                delay(1000L)
                _restTimerSeconds.value -= 1
            }
            // Timer selesai
            _isTimerRunning.value = false
        }
    }

    /** Hentikan rest timer */
    fun stopRestTimer() {
        timerJob?.cancel()
        _restTimerSeconds.value = 0
        _isTimerRunning.value = false
    }

    /** Akhiri sesi latihan aktif */
    fun endWorkout() {
        stopRestTimer()
        _currentSessionId.value = null
    }
}
