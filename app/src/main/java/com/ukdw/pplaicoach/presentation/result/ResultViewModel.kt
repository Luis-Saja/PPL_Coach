package com.ukdw.pplaicoach.presentation.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ukdw.pplaicoach.PPLAICoachApp
import com.ukdw.pplaicoach.data.repository.WorkoutRepository
import com.ukdw.pplaicoach.domain.model.InferenceResult
import com.ukdw.pplaicoach.domain.model.PrevPerformance
import com.ukdw.pplaicoach.domain.model.UserInput
import com.ukdw.pplaicoach.domain.rules.InferenceEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * === RESULT VIEW MODEL (ENHANCED) ===
 *
 * ViewModel untuk layar hasil inferensi PPL AI Coach.
 * Mengelola proses inferensi multi-langkah dengan animasi status real-time.
 *
 * ENHANCEMENT:
 * - Multi-step loading animation dengan status per fase inferensi
 * - Confidence score tracking
 * - Inference trace untuk full explainability
 *
 * Alur kerja:
 * 1. Menerima UserInput dari layar input
 * 2. Menjalankan animasi multi-step AI processing
 * 3. Menjalankan Inference Engine
 * 4. Menyimpan hasil ke Room DB melalui repository
 */
class ResultViewModel(application: Application) : AndroidViewModel(application) {

    // Mendapatkan repository dari Application class (singleton)
    private val repository: WorkoutRepository =
        (application as PPLAICoachApp).repository

    // Instance Inference Engine
    private val inferenceEngine = InferenceEngine()

    // ========================
    // STATE FLOWS
    // ========================

    /** Hasil inferensi dari Inference Engine. Null jika belum dijalankan. */
    private val _inferenceResult = MutableStateFlow<InferenceResult?>(null)
    val inferenceResult: StateFlow<InferenceResult?> = _inferenceResult.asStateFlow()

    /** Data input pengguna yang sedang diproses. Null jika belum ada. */
    private val _userInput = MutableStateFlow<UserInput?>(null)
    val userInput: StateFlow<UserInput?> = _userInput.asStateFlow()

    /** Status loading — true saat proses inferensi sedang berjalan */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Status penyimpanan — true jika sesi sudah berhasil disimpan ke database */
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    /** Peringatan penurunan performa */
    private val _showDecreaseWarning = MutableStateFlow(false)
    val showDecreaseWarning: StateFlow<Boolean> = _showDecreaseWarning.asStateFlow()

    /**
     * Status langkah AI processing saat ini.
     * Digunakan untuk animasi multi-step loading di UI.
     * Contoh: "Menginisialisasi Working Memory...",
     *         "Mengevaluasi 6 Production Rules...", dll.
     */
    private val _currentAIStep = MutableStateFlow("")
    val currentAIStep: StateFlow<String> = _currentAIStep.asStateFlow()

    /**
     * Index langkah saat ini (0-based) untuk progress indicator.
     * Total 6 langkah animasi.
     */
    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    // Daftar langkah AI processing untuk animasi
    private val aiSteps = listOf(
        "🧠 Menginisialisasi Working Memory...",
        "📊 Memuat fakta dari 4 variabel input...",
        "🔍 Mengevaluasi 6 Production Rules...",
        "⚖️ Menjalankan Conflict Resolution...",
        "🔥 Mengeksekusi Forward Chaining...",
        "✅ Menghitung Confidence Score..."
    )

    /** Total langkah AI processing */
    val totalSteps: Int = aiSteps.size

    // ========================
    // FUNGSI UTAMA
    // ========================

    /**
     * Menjalankan proses inferensi Forward Chaining dengan animasi multi-step.
     *
     * Langkah-langkah:
     * 1. Simpan input ke state
     * 2. Jalankan animasi multi-step (6 langkah × ~250ms = ~1.5 detik)
     * 3. Jalankan Inference Engine (di langkah ke-5)
     * 4. Simpan hasil ke state
     */
    fun runInference(input: UserInput) {
        viewModelScope.launch {
            // Reset state
            _userInput.value = input
            _isLoading.value = true
            _isSaved.value = false
            _inferenceResult.value = null
            _currentStepIndex.value = 0

            // Animasi multi-step AI processing
            for ((index, step) in aiSteps.withIndex()) {
                _currentAIStep.value = step
                _currentStepIndex.value = index

                if (index == 4) {
                    // Langkah ke-5: Jalankan Inference Engine sesungguhnya
                    delay(300L)
                    val result = inferenceEngine.run(input)
                    _inferenceResult.value = result
                    delay(200L)
                } else {
                    // Langkah lainnya: delay untuk efek visual
                    delay(250L)
                }
            }

            // Selesai — sedikit delay sebelum menampilkan hasil
            _currentAIStep.value = "🎯 Rekomendasi siap!"
            _currentStepIndex.value = aiSteps.size
            delay(300L)

            _isLoading.value = false

            // Cek peringatan penurunan performa
            _showDecreaseWarning.value =
                input.prevPerformance == PrevPerformance.DECREASE
        }
    }

    /**
     * Menyimpan sesi latihan ke Room Database.
     */
    fun saveSession() {
        val input = _userInput.value ?: return
        val result = _inferenceResult.value ?: return

        viewModelScope.launch {
            repository.saveSession(input, result)
            _isSaved.value = true
        }
    }

    /** Menutup peringatan penurunan performa */
    fun dismissDecreaseWarning() {
        _showDecreaseWarning.value = false
    }

    /** Mereset state untuk sesi baru */
    fun resetState() {
        _inferenceResult.value = null
        _userInput.value = null
        _isLoading.value = false
        _isSaved.value = false
        _showDecreaseWarning.value = false
        _currentAIStep.value = ""
        _currentStepIndex.value = 0
    }
}
