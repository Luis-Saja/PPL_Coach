package com.ukdw.pplaicoach.presentation.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ukdw.pplaicoach.PPLAICoachApp
import com.ukdw.pplaicoach.data.local.WorkoutSessionEntity
import com.ukdw.pplaicoach.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.*

/**
 * === HOME VIEW MODEL (ENHANCED) ===
 *
 * ViewModel untuk layar beranda PPL AI Coach.
 *
 * ENHANCEMENT:
 * - AI Insight: Analisis tren dari riwayat sesi latihan
 * - Deteksi pola: rest day berturut-turut, tren positif/negatif
 * - Semua state menggunakan StateFlow agar reaktif
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository =
        (application as PPLAICoachApp).repository

    private val prefs = application.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    /** Nama pengguna */
    val userName: StateFlow<String> = MutableStateFlow(
        prefs.getString(KEY_USER_NAME, "Athlete") ?: "Athlete"
    )

    /** Sesi latihan terakhir */
    val lastSession: StateFlow<WorkoutSessionEntity?> = repository
        .getRecentSessions(1)
        .map { sessions -> sessions.firstOrNull() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /** Total jumlah sesi */
    val totalSessions: StateFlow<Int> = repository
        .getSessionCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    /** Rata-rata load adjustment 7 hari terakhir */
    val weeklyAvgLoad: StateFlow<Double> = run {
        val sevenDaysAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        repository
            .getAverageLoadAdjustmentSince(sevenDaysAgoMillis)
            .map { avg -> avg ?: 0.0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0.0
            )
    }

    /**
     * AI Insight — Analisis tren dari 5 sesi terakhir.
     * Sistem pakar menganalisis pola latihan dan memberikan insight cerdas.
     */
    val aiInsight: StateFlow<String> = repository
        .getRecentSessions(5)
        .map { sessions -> generateAIInsight(sessions) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ""
        )

    /**
     * Menghasilkan AI Insight berdasarkan analisis pola dari sesi-sesi terakhir.
     * Ini mensimulasikan kemampuan AI untuk mendeteksi tren dan memberikan saran proaktif.
     */
    private fun generateAIInsight(sessions: List<WorkoutSessionEntity>): String {
        if (sessions.isEmpty()) return ""
        if (sessions.size < 2) return "🧠 AI membutuhkan minimal 2 sesi untuk menganalisis tren latihanmu."

        // Analisis 1: Cek apakah ada rest day berturut-turut
        val recentRecs = sessions.take(3).map { it.recommendation }
        if (recentRecs.count { it == "REST_DAY" } >= 2) {
            return "⚠️ AI mendeteksi pola: Kamu mendapat rekomendasi Rest Day 2 kali berturut-turut. " +
                    "Ini mungkin tanda bahwa kualitas tidur atau recovery perlu diperbaiki. " +
                    "Pertimbangkan untuk menambah durasi tidur dan mengurangi intensitas."
        }

        // Analisis 2: Tren load adjustment
        val loadTrend = sessions.take(3).map { it.loadAdjustment }
        if (loadTrend.all { it > 0 }) {
            return "📈 AI mendeteksi tren positif! Load adjustment meningkat dalam ${loadTrend.size} sesi terakhir. " +
                    "Progressive overload berjalan dengan baik. Pastikan nutrisi dan recovery tetap optimal."
        }

        if (loadTrend.all { it < 0 }) {
            return "📉 AI mendeteksi tren penurunan: Load adjustment negatif dalam ${loadTrend.size} sesi terakhir. " +
                    "Sistem merekomendasikan evaluasi kualitas tidur dan tingkat stres."
        }

        // Analisis 3: Konsistensi goal
        val goals = sessions.map { it.userGoal }.distinct()
        if (goals.size > 1 && sessions.size >= 3) {
            return "🔄 AI mendeteksi pergantian goal (${goals.joinToString(" & ")}). " +
                    "Konsistensi tujuan latihan membantu AI memberikan rekomendasi yang lebih akurat."
        }

        // Analisis 4: Pola tidur
        val avgSleep = sessions.map { it.sleepDuration }.average()
        if (avgSleep < 6.0) {
            return "😴 AI mendeteksi rata-rata tidurmu ${String.format("%.1f", avgSleep)} jam dari ${sessions.size} sesi terakhir. " +
                    "Durasi tidur < 6 jam secara konsisten akan memicu lebih banyak recovery rules."
        }

        if (avgSleep > 7.5) {
            return "✅ AI mendeteksi pola tidur yang baik (rata-rata ${String.format("%.1f", avgSleep)} jam). " +
                    "Tidur berkualitas mendukung progressive overload dan recovery optimal."
        }

        // Default insight
        val lastRec = sessions.first().recommendation.replace("_", " ")
        return "🧠 AI telah menganalisis ${sessions.size} sesi terakhirmu. " +
                "Sesi terakhir: $lastRec. Konsistensi adalah kunci — tetap berlatih!"
    }

    /** Memuat ulang nama pengguna dari SharedPreferences */
    fun refreshUserName() {
        val currentName = prefs.getString(KEY_USER_NAME, "Athlete") ?: "Athlete"
        (userName as MutableStateFlow).value = currentName
    }

    companion object {
        private const val PREFS_NAME = "ppl_ai_coach_prefs"
        private const val KEY_USER_NAME = "user_name"
    }
}
