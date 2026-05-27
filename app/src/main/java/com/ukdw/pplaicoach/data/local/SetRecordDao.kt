package com.ukdw.pplaicoach.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * === SET RECORD DAO ===
 * Operasi CRUD untuk tabel set_records.
 * Menyediakan query untuk mencatat set dan mengambil data progress per exercise.
 */
@Dao
interface SetRecordDao {

    /** Menyisipkan satu set record */
    @Insert
    suspend fun insertSetRecord(record: SetRecordEntity)

    /** Mengambil semua set dalam satu sesi */
    @Query("SELECT * FROM set_records WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getRecordsBySession(sessionId: Long): Flow<List<SetRecordEntity>>

    /**
     * Mengambil data progress untuk satu exercise (untuk chart).
     * Mengembalikan max weight per sesi, diurutkan berdasarkan tanggal sesi.
     */
    @Query("""
        SELECT sr.id, sr.exerciseId, sr.sessionId, 
               MAX(sr.weightInKg) as weightInKg, 
               MAX(sr.reps) as reps,
               sr.intensityType,
               ts.date
        FROM set_records sr
        INNER JOIN tracker_sessions ts ON sr.sessionId = ts.id
        WHERE sr.exerciseId = :exerciseId
        GROUP BY sr.sessionId
        ORDER BY ts.date ASC
    """)
    fun getProgressForExercise(exerciseId: Int): Flow<List<ProgressPoint>>

    /** Menghapus satu set record */
    @Query("DELETE FROM set_records WHERE id = :id")
    suspend fun deleteSetRecord(id: Long)
}

/**
 * Data class untuk hasil query progress chart.
 * Berisi max weight dan reps per sesi untuk satu exercise.
 */
data class ProgressPoint(
    val id: Long,
    val exerciseId: Int,
    val sessionId: Long,
    val weightInKg: Double,
    val reps: Int,
    val intensityType: String,
    val date: Long
)
