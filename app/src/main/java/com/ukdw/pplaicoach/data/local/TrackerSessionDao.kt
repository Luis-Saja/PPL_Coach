package com.ukdw.pplaicoach.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * === TRACKER SESSION DAO ===
 * Operasi CRUD untuk tabel tracker_sessions.
 * Digunakan untuk auto-suggest hari PPL berikutnya berdasarkan sesi terakhir.
 */
@Dao
interface TrackerSessionDao {

    /** Menyisipkan sesi baru, mengembalikan ID yang di-generate */
    @Insert
    suspend fun insertSession(session: TrackerSessionEntity): Long

    /** Mengambil sesi terakhir (untuk auto-suggest hari PPL berikutnya) */
    @Query("SELECT * FROM tracker_sessions ORDER BY date DESC LIMIT 1")
    fun getLatestSession(): Flow<TrackerSessionEntity?>

    /** Mengambil semua sesi tracker, diurutkan dari terbaru */
    @Query("SELECT * FROM tracker_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<TrackerSessionEntity>>

    /** Menghapus satu sesi berdasarkan ID */
    @Query("DELETE FROM tracker_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}
