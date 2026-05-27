package com.ukdw.pplaicoach.data.repository

import com.ukdw.pplaicoach.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * === TRACKER REPOSITORY ===
 * Repository untuk fitur Workout Tracker.
 * Mengelola data exercise, sesi tracker, dan set records.
 * Terpisah dari WorkoutRepository (yang digunakan oleh Expert System AI).
 *
 * Fitur utama:
 * - Auto-suggest hari PPL berikutnya (Push→Pull→Legs→Push)
 * - CRUD sesi tracker dan set records
 * - Progress data untuk chart
 */
class TrackerRepository(
    private val exerciseDao: ExerciseDao,
    private val trackerSessionDao: TrackerSessionDao,
    private val setRecordDao: SetRecordDao
) {
    // ========================
    // EXERCISE QUERIES
    // ========================

    /** Mengambil semua exercise */
    fun getAllExercises(): Flow<List<ExerciseEntity>> =
        exerciseDao.getAllExercises()

    /** Mengambil exercise berdasarkan kategori PPL */
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<ExerciseEntity>> =
        exerciseDao.getExercisesByCategory(category.name)

    /** Mengambil satu exercise berdasarkan ID */
    suspend fun getExerciseById(id: Int): ExerciseEntity? =
        exerciseDao.getExerciseById(id)

    // ========================
    // AUTO-SUGGEST LOGIC
    // Logika rotasi PPL: Push → Pull → Legs → Push
    // Jika belum pernah latihan, default ke PUSH.
    // ========================

    /**
     * Menghitung hari PPL yang disarankan berdasarkan sesi terakhir.
     * Menggunakan rotasi sederhana: setelah Push → Pull, setelah Pull → Legs, dst.
     *
     * @return Flow<ExerciseCategory> hari latihan yang disarankan
     */
    fun getSuggestedDay(): Flow<ExerciseCategory> =
        trackerSessionDao.getLatestSession().map { latestSession ->
            if (latestSession == null) {
                // Belum pernah latihan → default Push
                ExerciseCategory.PUSH
            } else {
                // Rotasi PPL berdasarkan sesi terakhir
                when (latestSession.category) {
                    ExerciseCategory.PUSH.name -> ExerciseCategory.PULL
                    ExerciseCategory.PULL.name -> ExerciseCategory.LEGS
                    ExerciseCategory.LEGS.name -> ExerciseCategory.PUSH
                    else -> ExerciseCategory.PUSH
                }
            }
        }

    // ========================
    // SESSION MANAGEMENT
    // ========================

    /** Memulai sesi tracker baru, mengembalikan session ID */
    suspend fun startSession(category: ExerciseCategory): Long {
        val session = TrackerSessionEntity(category = category.name)
        return trackerSessionDao.insertSession(session)
    }

    /** Mengambil semua sesi tracker */
    fun getAllSessions(): Flow<List<TrackerSessionEntity>> =
        trackerSessionDao.getAllSessions()

    /** Menghapus satu sesi tracker */
    suspend fun deleteSession(sessionId: Long) =
        trackerSessionDao.deleteSession(sessionId)

    // ========================
    // SET RECORD MANAGEMENT
    // ========================

    /** Mencatat satu set latihan */
    suspend fun logSet(
        exerciseId: Int,
        sessionId: Long,
        weightInKg: Double,
        reps: Int,
        intensityType: IntensityType = IntensityType.NORMAL
    ) {
        val record = SetRecordEntity(
            exerciseId = exerciseId,
            sessionId = sessionId,
            weightInKg = weightInKg,
            reps = reps,
            intensityType = intensityType.name
        )
        setRecordDao.insertSetRecord(record)
    }

    /** Mengambil semua set dalam satu sesi */
    fun getRecordsBySession(sessionId: Long): Flow<List<SetRecordEntity>> =
        setRecordDao.getRecordsBySession(sessionId)

    /** Mengambil data progress untuk chart (max weight per sesi) */
    fun getProgressForExercise(exerciseId: Int): Flow<List<ProgressPoint>> =
        setRecordDao.getProgressForExercise(exerciseId)

    /** Menghapus satu set record */
    suspend fun deleteSetRecord(id: Long) =
        setRecordDao.deleteSetRecord(id)
}
