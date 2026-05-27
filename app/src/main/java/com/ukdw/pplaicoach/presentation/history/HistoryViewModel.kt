package com.ukdw.pplaicoach.presentation.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ukdw.pplaicoach.PPLAICoachApp
import com.ukdw.pplaicoach.data.local.WorkoutSessionEntity
import com.ukdw.pplaicoach.data.repository.WorkoutRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * === HISTORY VIEW MODEL ===
 *
 * ViewModel untuk layar riwayat sesi latihan PPL AI Coach.
 * Mengelola daftar sesi latihan yang tersimpan dengan kemampuan filter.
 *
 * Fitur:
 * - Menampilkan semua sesi latihan dari Room DB
 * - Filter berdasarkan tipe rekomendasi (Rest Day, Light Training, Normal Training)
 * - Filter "Semua" untuk menampilkan seluruh riwayat
 * - Hapus satu sesi tertentu
 *
 * Semua state menggunakan StateFlow agar UI reaktif terhadap perubahan data.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    // Mendapatkan repository dari Application class (singleton)
    private val repository: WorkoutRepository =
        (application as PPLAICoachApp).repository

    // ========================
    // FILTER STATE
    // ========================

    /**
     * Filter yang sedang aktif.
     * Nilai yang mungkin:
     * - "Semua" — Menampilkan seluruh riwayat
     * - "REST_DAY" — Hanya sesi dengan rekomendasi Rest Day
     * - "LIGHT_TRAINING" — Hanya sesi dengan rekomendasi Light Training
     * - "NORMAL_TRAINING" — Hanya sesi dengan rekomendasi Normal Training
     */
    private val _selectedFilter = MutableStateFlow(FILTER_ALL)
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    // ========================
    // DAFTAR SESI (DENGAN FILTER)
    // ========================

    /**
     * Daftar sesi latihan yang sudah difilter.
     * Menggunakan flatMapLatest agar secara otomatis berpindah
     * ke query database yang sesuai saat filter berubah.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val sessions: StateFlow<List<WorkoutSessionEntity>> = _selectedFilter
        .flatMapLatest { filter ->
            if (filter == FILTER_ALL) {
                // Ambil semua sesi jika filter "Semua"
                repository.getAllSessions()
            } else {
                // Ambil sesi berdasarkan tipe rekomendasi
                repository.getSessionsByRecommendation(filter)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ========================
    // FUNGSI FILTER
    // ========================

    /**
     * Mengubah filter yang aktif.
     * Daftar sesi akan otomatis diperbarui sesuai filter baru
     * melalui flatMapLatest di atas.
     *
     * @param filter Nilai filter: "Semua", "REST_DAY", "LIGHT_TRAINING", atau "NORMAL_TRAINING"
     */
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // ========================
    // FUNGSI HAPUS SESI
    // ========================

    /**
     * Menghapus satu sesi latihan dari database berdasarkan ID.
     * Daftar sesi akan otomatis diperbarui karena menggunakan Flow dari Room.
     *
     * @param id Primary key dari sesi yang akan dihapus
     */
    fun deleteSession(id: Int) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }

    companion object {
        /** Konstanta untuk filter "Semua" (menampilkan seluruh riwayat) */
        const val FILTER_ALL = "Semua"

        /** Daftar semua pilihan filter yang tersedia */
        val FILTER_OPTIONS = listOf(
            FILTER_ALL,
            "REST_DAY",
            "LIGHT_TRAINING",
            "NORMAL_TRAINING"
        )

        /**
         * Mendapatkan label tampilan untuk setiap filter.
         * Digunakan di UI untuk menampilkan teks yang ramah pengguna.
         */
        fun getFilterDisplayName(filter: String): String {
            return when (filter) {
                FILTER_ALL -> "Semua"
                "REST_DAY" -> "Rest Day"
                "LIGHT_TRAINING" -> "Light Training"
                "NORMAL_TRAINING" -> "Normal Training"
                else -> filter
            }
        }
    }
}
