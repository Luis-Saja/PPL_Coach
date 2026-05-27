package com.ukdw.pplaicoach.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) untuk tabel workout_sessions.
 * Menyediakan operasi CRUD untuk riwayat sesi latihan.
 */
@Dao
interface WorkoutSessionDao {

    /** Menyisipkan sesi latihan baru ke database */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)

    /** Mengambil semua sesi, diurutkan dari yang terbaru */
    @Query("SELECT * FROM workout_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    /** Mengambil N sesi terakhir */
    @Query("SELECT * FROM workout_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<WorkoutSessionEntity>>

    /** Menghitung total jumlah sesi tersimpan */
    @Query("SELECT COUNT(*) FROM workout_sessions")
    fun getSessionCount(): Flow<Int>

    /** Mengambil sesi berdasarkan tipe rekomendasi */
    @Query("SELECT * FROM workout_sessions WHERE recommendation = :type ORDER BY timestamp DESC")
    fun getSessionsByRecommendation(type: String): Flow<List<WorkoutSessionEntity>>

    /** Menghapus satu sesi berdasarkan ID */
    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Int)

    /** Menghapus seluruh riwayat sesi (untuk fitur reset di profil) */
    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAllSessions()

    /** Mengambil rata-rata load adjustment dari 7 hari terakhir */
    @Query("""
        SELECT AVG(loadAdjustment) FROM workout_sessions 
        WHERE timestamp >= :since
    """)
    fun getAverageLoadAdjustmentSince(since: Long): Flow<Double?>
}
