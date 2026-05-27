package com.ukdw.pplaicoach.presentation.input

import androidx.lifecycle.ViewModel
import com.ukdw.pplaicoach.domain.model.*
import kotlinx.coroutines.flow.*

/**
 * === INPUT VIEW MODEL ===
 *
 * ViewModel untuk layar input harian PPL AI Coach.
 * Mengelola state form input kondisi fisik pengguna (4 variabel sistem pakar):
 * 1. Durasi tidur (jam, numerik)
 * 2. Tingkat nyeri otot (SorenessLevel)
 * 3. Tujuan latihan (UserGoal)
 * 4. Performa sesi sebelumnya (PrevPerformance)
 *
 * Menyediakan:
 * - Validasi: memastikan semua field terisi sebelum submit
 * - Deteksi inkonsistensi: peringatan jika performa menurun tapi soreness rendah
 * - Builder: membentuk objek UserInput dari state form
 *
 * Semua state menggunakan StateFlow agar UI reaktif.
 */
class InputViewModel : ViewModel() {

    // ========================
    // STATE FORM INPUT
    // ========================

    /** Durasi tidur pengguna dalam jam (contoh: 7.5) */
    private val _sleepDuration = MutableStateFlow(7.0)
    val sleepDuration: StateFlow<Double> = _sleepDuration.asStateFlow()

    /** Tingkat nyeri otot (DOMS). Null berarti belum dipilih. */
    private val _muscleSoreness = MutableStateFlow<SorenessLevel?>(null)
    val muscleSoreness: StateFlow<SorenessLevel?> = _muscleSoreness.asStateFlow()

    /** Tujuan latihan pengguna. Null berarti belum dipilih. */
    private val _userGoal = MutableStateFlow<UserGoal?>(null)
    val userGoal: StateFlow<UserGoal?> = _userGoal.asStateFlow()

    /** Performa sesi sebelumnya. Null berarti belum dipilih. */
    private val _prevPerformance = MutableStateFlow<PrevPerformance?>(null)
    val prevPerformance: StateFlow<PrevPerformance?> = _prevPerformance.asStateFlow()

    // ========================
    // DERIVED STATE: VALIDASI
    // ========================

    /**
     * Mengecek apakah semua field sudah terisi dengan valid.
     * Durasi tidur harus antara 0–24 jam, dan ketiga pilihan enum harus sudah dipilih.
     * Digunakan untuk mengaktifkan/menonaktifkan tombol submit.
     */
    val allFieldsFilled: StateFlow<Boolean> = combine(
        _sleepDuration, _muscleSoreness, _userGoal, _prevPerformance
    ) { sleep, soreness, goal, performance ->
        sleep in 0.0..24.0 &&
                soreness != null &&
                goal != null &&
                performance != null
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    // ========================
    // FUNGSI UPDATE STATE
    // ========================

    /** Mengubah nilai durasi tidur */
    fun updateSleepDuration(value: Double) {
        _sleepDuration.value = value
    }

    /** Mengubah pilihan tingkat nyeri otot */
    fun updateMuscleSoreness(level: SorenessLevel) {
        _muscleSoreness.value = level
    }

    /** Mengubah pilihan tujuan latihan */
    fun updateUserGoal(goal: UserGoal) {
        _userGoal.value = goal
    }

    /** Mengubah pilihan performa sesi sebelumnya */
    fun updatePrevPerformance(performance: PrevPerformance) {
        _prevPerformance.value = performance
    }

    /**
     * Mengatur tujuan latihan default dari preferensi pengguna.
     * Dipanggil saat layar input pertama kali dibuka agar
     * pilihan default sudah terisi sesuai pengaturan profil.
     */
    fun setDefaultGoal(goal: UserGoal) {
        // Hanya set jika belum ada pilihan (agar tidak menimpa pilihan manual)
        if (_userGoal.value == null) {
            _userGoal.value = goal
        }
    }

    // ========================
    // DETEKSI INKONSISTENSI
    // ========================

    /**
     * Mengecek inkonsistensi input pengguna.
     * Mengembalikan true jika pengguna melaporkan performa MENURUN
     * tetapi tingkat nyeri otot LOW (tidak ada rasa sakit).
     *
     * Ini bisa menandakan:
     * - Pengguna salah memilih tingkat nyeri
     * - Penurunan performa bukan karena faktor fisik (mungkin mental/teknik)
     *
     * UI akan menampilkan peringatan/dialog jika true.
     */
    fun checkInconsistency(): Boolean {
        return _prevPerformance.value == PrevPerformance.DECREASE &&
                _muscleSoreness.value == SorenessLevel.LOW
    }

    // ========================
    // BUILD USER INPUT
    // ========================

    /**
     * Membentuk objek UserInput dari state form saat ini.
     * Hanya dipanggil setelah allFieldsFilled == true.
     *
     * @return UserInput yang siap diproses oleh Inference Engine
     * @throws IllegalStateException jika ada field yang masih null
     */
    fun buildUserInput(): UserInput {
        return UserInput(
            sleepDuration = _sleepDuration.value,
            muscleSoreness = _muscleSoreness.value
                ?: error("Muscle soreness belum dipilih"),
            userGoal = _userGoal.value
                ?: error("User goal belum dipilih"),
            prevPerformance = _prevPerformance.value
                ?: error("Previous performance belum dipilih")
        )
    }

    /**
     * Mereset seluruh form ke state awal.
     * Dipanggil setelah pengguna berhasil submit dan ingin input data baru.
     */
    fun resetForm() {
        _sleepDuration.value = 7.0
        _muscleSoreness.value = null
        _userGoal.value = null
        _prevPerformance.value = null
    }
}
