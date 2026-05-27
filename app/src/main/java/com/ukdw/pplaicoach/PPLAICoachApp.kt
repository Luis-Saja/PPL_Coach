package com.ukdw.pplaicoach

import android.app.Application
import com.ukdw.pplaicoach.data.local.AppDatabase
import com.ukdw.pplaicoach.data.repository.TrackerRepository
import com.ukdw.pplaicoach.data.repository.WorkoutRepository

/**
 * Application class untuk PPL AI Coach.
 * Menginisialisasi database dan repository sebagai singleton.
 * Menyediakan akses ke kedua repository:
 * - WorkoutRepository: untuk fitur Expert System AI
 * - TrackerRepository: untuk fitur Workout Tracker (tracking set/rep/berat)
 */
class PPLAICoachApp : Application() {

    /** Instance database Room (lazy initialization) */
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    /** Repository untuk fitur Expert System AI */
    val repository: WorkoutRepository by lazy {
        WorkoutRepository(database.workoutSessionDao())
    }

    /** Repository untuk fitur Workout Tracker */
    val trackerRepository: TrackerRepository by lazy {
        TrackerRepository(
            exerciseDao = database.exerciseDao(),
            trackerSessionDao = database.trackerSessionDao(),
            setRecordDao = database.setRecordDao()
        )
    }
}
