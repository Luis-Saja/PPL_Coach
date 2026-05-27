package com.ukdw.pplaicoach.data.repository

import com.google.gson.Gson
import com.ukdw.pplaicoach.data.local.WorkoutSessionDao
import com.ukdw.pplaicoach.data.local.WorkoutSessionEntity
import com.ukdw.pplaicoach.domain.model.InferenceResult
import com.ukdw.pplaicoach.domain.model.UserInput
import kotlinx.coroutines.flow.Flow

/**
 * Repository untuk mengelola data sesi latihan.
 * Bertindak sebagai abstraksi antara domain layer dan data layer (Room DB).
 * Semua operasi database diakses melalui repository ini.
 */
class WorkoutRepository(
    private val dao: WorkoutSessionDao
) {
    private val gson = Gson()

    /**
     * Menyimpan sesi latihan baru ke database.
     * Menggabungkan data input pengguna dengan hasil inferensi.
     *
     * @param input Data kondisi fisik yang dimasukkan pengguna
     * @param result Hasil inferensi dari Inference Engine
     */
    suspend fun saveSession(input: UserInput, result: InferenceResult) {
        val entity = WorkoutSessionEntity(
            timestamp = System.currentTimeMillis(),
            sleepDuration = input.sleepDuration,
            muscleSoreness = input.muscleSoreness.name,
            userGoal = input.userGoal.name,
            prevPerformance = input.prevPerformance.name,
            loadAdjustment = result.loadAdjustment,
            volumeAdjustment = result.volumeAdjustment,
            recommendation = result.recommendation.name,
            activeRules = gson.toJson(result.activeRules),
            explanationText = result.explanations.joinToString("\n\n")
        )
        dao.insertSession(entity)
    }

    /** Mengambil semua sesi latihan (terbaru di atas) */
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>> {
        return dao.getAllSessions()
    }

    /** Mengambil N sesi terakhir */
    fun getRecentSessions(limit: Int): Flow<List<WorkoutSessionEntity>> {
        return dao.getRecentSessions(limit)
    }

    /** Menghitung total jumlah sesi */
    fun getSessionCount(): Flow<Int> {
        return dao.getSessionCount()
    }

    /** Mengambil sesi berdasarkan tipe rekomendasi */
    fun getSessionsByRecommendation(type: String): Flow<List<WorkoutSessionEntity>> {
        return dao.getSessionsByRecommendation(type)
    }

    /** Menghapus satu sesi */
    suspend fun deleteSession(sessionId: Int) {
        dao.deleteSession(sessionId)
    }

    /** Menghapus seluruh riwayat */
    suspend fun deleteAllSessions() {
        dao.deleteAllSessions()
    }

    /** Rata-rata load adjustment sejak waktu tertentu */
    fun getAverageLoadAdjustmentSince(since: Long): Flow<Double?> {
        return dao.getAverageLoadAdjustmentSince(since)
    }
}
