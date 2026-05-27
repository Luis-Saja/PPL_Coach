package com.ukdw.pplaicoach.presentation.tracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ukdw.pplaicoach.PPLAICoachApp
import com.ukdw.pplaicoach.data.local.ExerciseEntity
import com.ukdw.pplaicoach.data.local.ProgressPoint
import com.ukdw.pplaicoach.data.repository.TrackerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * === PROGRESS VIEW MODEL ===
 *
 * ViewModel untuk layar Progress Chart.
 * Mengelola state untuk:
 * - Daftar semua exercise (untuk dropdown selector)
 * - Data progress per exercise (max weight per sesi untuk chart)
 * - Statistik ringkasan (personal best, total sets, dll)
 */
class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrackerRepository =
        (application as PPLAICoachApp).trackerRepository

    /** Daftar semua exercise untuk dropdown selector */
    val exercises: StateFlow<List<ExerciseEntity>> = repository
        .getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Exercise yang sedang dipilih untuk ditampilkan chart-nya */
    private val _selectedExercise = MutableStateFlow<ExerciseEntity?>(null)
    val selectedExercise: StateFlow<ExerciseEntity?> = _selectedExercise.asStateFlow()

    /** Data progress untuk chart (max weight per sesi) */
    val progressData: StateFlow<List<ProgressPoint>> = _selectedExercise
        .flatMapLatest { exercise ->
            if (exercise != null) {
                repository.getProgressForExercise(exercise.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Pilih exercise untuk ditampilkan data progress-nya */
    fun selectExercise(exercise: ExerciseEntity) {
        _selectedExercise.value = exercise
    }
}
