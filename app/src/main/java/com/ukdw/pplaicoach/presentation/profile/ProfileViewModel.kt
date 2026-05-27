package com.ukdw.pplaicoach.presentation.profile

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ukdw.pplaicoach.PPLAICoachApp
import com.ukdw.pplaicoach.data.repository.WorkoutRepository
import com.ukdw.pplaicoach.domain.model.UserGoal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * === PROFILE VIEW MODEL ===
 *
 * ViewModel untuk layar profil dan pengaturan PPL AI Coach.
 * Mengelola data preferensi pengguna yang disimpan di SharedPreferences
 * serta operasi reset riwayat melalui repository.
 *
 * Data yang dikelola:
 * - Nama pengguna (SharedPreferences)
 * - Tujuan latihan default / Bulking atau Cutting (SharedPreferences)
 * - Reset seluruh riwayat sesi latihan (Room DB via repository)
 *
 * Semua state menggunakan StateFlow agar UI reaktif.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // Mendapatkan repository dari Application class (singleton)
    private val repository: WorkoutRepository =
        (application as PPLAICoachApp).repository

    // SharedPreferences untuk menyimpan preferensi pengguna secara lokal
    private val prefs = application.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    // ========================
    // STATE: NAMA PENGGUNA
    // ========================

    /**
     * Nama pengguna yang ditampilkan di profil.
     * Disimpan di SharedPreferences agar persisten antar sesi aplikasi.
     * Default: "Athlete" jika belum pernah diisi.
     */
    private val _userName = MutableStateFlow(
        prefs.getString(KEY_USER_NAME, "Athlete") ?: "Athlete"
    )
    val userName: StateFlow<String> = _userName.asStateFlow()

    // ========================
    // STATE: TUJUAN LATIHAN DEFAULT
    // ========================

    /**
     * Tujuan latihan default pengguna (Bulking/Cutting).
     * Akan otomatis terpilih di form input harian.
     * Disimpan di SharedPreferences agar persisten.
     * Default: BULKING jika belum pernah dipilih.
     */
    private val _defaultGoal = MutableStateFlow(
        loadDefaultGoal()
    )
    val defaultGoal: StateFlow<UserGoal> = _defaultGoal.asStateFlow()

    // ========================
    // FUNGSI UPDATE PREFERENSI
    // ========================

    /**
     * Memperbarui nama pengguna.
     * Langsung menyimpan ke SharedPreferences agar persisten.
     *
     * @param name Nama baru pengguna
     */
    fun updateUserName(name: String) {
        _userName.value = name
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    /**
     * Memperbarui tujuan latihan default.
     * Langsung menyimpan ke SharedPreferences agar persisten.
     *
     * @param goal Tujuan latihan baru (BULKING atau CUTTING)
     */
    fun updateDefaultGoal(goal: UserGoal) {
        _defaultGoal.value = goal
        prefs.edit().putString(KEY_DEFAULT_GOAL, goal.name).apply()
    }

    // ========================
    // FUNGSI RESET RIWAYAT
    // ========================

    /**
     * Menghapus seluruh riwayat sesi latihan dari database.
     * Tindakan ini bersifat permanen dan tidak dapat dibatalkan.
     * Hanya menghapus data sesi, tidak menghapus preferensi pengguna.
     */
    fun resetHistory() {
        viewModelScope.launch {
            repository.deleteAllSessions()
        }
    }

    // ========================
    // HELPER INTERNAL
    // ========================

    /**
     * Memuat tujuan latihan default dari SharedPreferences.
     * Mencoba mengkonversi string yang tersimpan menjadi enum UserGoal.
     * Jika gagal (string tidak valid), mengembalikan BULKING sebagai default.
     */
    private fun loadDefaultGoal(): UserGoal {
        val goalString = prefs.getString(KEY_DEFAULT_GOAL, UserGoal.BULKING.name)
        return try {
            UserGoal.valueOf(goalString ?: UserGoal.BULKING.name)
        } catch (e: IllegalArgumentException) {
            // Fallback ke BULKING jika nilai tersimpan tidak valid
            UserGoal.BULKING
        }
    }

    companion object {
        /** Nama file SharedPreferences (konsisten dengan HomeViewModel) */
        private const val PREFS_NAME = "ppl_ai_coach_prefs"

        /** Key untuk menyimpan nama pengguna */
        private const val KEY_USER_NAME = "user_name"

        /** Key untuk menyimpan tujuan latihan default */
        private const val KEY_DEFAULT_GOAL = "default_goal"
    }
}
