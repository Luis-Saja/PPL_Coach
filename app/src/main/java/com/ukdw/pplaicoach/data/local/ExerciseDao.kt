package com.ukdw.pplaicoach.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * === EXERCISE DAO ===
 * Operasi CRUD untuk tabel exercises.
 * Digunakan untuk mengambil daftar gerakan berdasarkan kategori PPL.
 */
@Dao
interface ExerciseDao {

    /** Mengambil semua gerakan */
    @Query("SELECT * FROM exercises ORDER BY category, name")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    /** Mengambil gerakan berdasarkan kategori PPL (PUSH/PULL/LEGS) */
    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name")
    fun getExercisesByCategory(category: String): Flow<List<ExerciseEntity>>

    /** Mengambil satu gerakan berdasarkan ID */
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Int): ExerciseEntity?

    /** Menyisipkan daftar gerakan (untuk pre-populate) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)
}
