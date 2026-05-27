package com.ukdw.pplaicoach.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ukdw.pplaicoach.presentation.history.HistoryScreen
import com.ukdw.pplaicoach.presentation.history.HistoryViewModel
import com.ukdw.pplaicoach.presentation.home.HomeScreen
import com.ukdw.pplaicoach.presentation.home.HomeViewModel
import com.ukdw.pplaicoach.presentation.input.InputScreen
import com.ukdw.pplaicoach.presentation.profile.ProfileScreen
import com.ukdw.pplaicoach.presentation.profile.ProfileViewModel
import com.ukdw.pplaicoach.presentation.result.ResultScreen
import com.ukdw.pplaicoach.presentation.result.ResultViewModel
import com.ukdw.pplaicoach.presentation.test.TestModeScreen
import com.ukdw.pplaicoach.presentation.tracker.ProgressScreen
import com.ukdw.pplaicoach.presentation.tracker.ProgressViewModel
import com.ukdw.pplaicoach.presentation.tracker.TrackerScreen
import com.ukdw.pplaicoach.presentation.tracker.TrackerViewModel

/**
 * === NAVIGATION GRAPH ===
 * Mengatur alur navigasi antar layar dalam aplikasi.
 * Menggunakan Jetpack Navigation Compose.
 */
@Composable
fun PPLNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    resultViewModel: ResultViewModel = viewModel()
) {
    Box(modifier = modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // ========================
        // HALAMAN HOME / DASHBOARD
        // ========================
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel()
            val userName by homeViewModel.userName.collectAsState()
            val lastSession by homeViewModel.lastSession.collectAsState()
            val totalSessions by homeViewModel.totalSessions.collectAsState()
            val weeklyAvgLoad by homeViewModel.weeklyAvgLoad.collectAsState()
            val aiInsight by homeViewModel.aiInsight.collectAsState()

            HomeScreen(
                userName = userName,
                lastSession = lastSession,
                totalSessions = totalSessions,
                weeklyAvgLoad = weeklyAvgLoad,
                aiInsight = aiInsight,
                onStartSession = {
                    navController.navigate(Screen.Input.route)
                },
                onViewHistory = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        // ========================
        // HALAMAN INPUT KONDISI FISIK
        // ========================
        composable(Screen.Input.route) {
            InputScreen(
                onSubmit = { userInput ->
                    // Jalankan inferensi dan navigasi ke result
                    resultViewModel.runInference(userInput)
                    navController.navigate(Screen.Result.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ========================
        // HALAMAN HASIL INFERENSI
        // ========================
        composable(Screen.Result.route) {
            val inferenceResult by resultViewModel.inferenceResult.collectAsState()
            val userInput by resultViewModel.userInput.collectAsState()
            val isLoading by resultViewModel.isLoading.collectAsState()
            val isSaved by resultViewModel.isSaved.collectAsState()
            val showDecreaseWarning by resultViewModel.showDecreaseWarning.collectAsState()
            val currentAIStep by resultViewModel.currentAIStep.collectAsState()
            val currentStepIndex by resultViewModel.currentStepIndex.collectAsState()

            ResultScreen(
                inferenceResult = inferenceResult,
                userInput = userInput,
                isSaved = isSaved,
                isLoading = isLoading,
                showDecreaseWarning = showDecreaseWarning,
                currentAIStep = currentAIStep,
                currentStepIndex = currentStepIndex,
                totalSteps = resultViewModel.totalSteps,
                onSave = { resultViewModel.saveSession() },
                onReInput = {
                    resultViewModel.resetState()
                    navController.popBackStack(Screen.Input.route, inclusive = false)
                },
                onViewHistory = {
                    resultViewModel.resetState()
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // ========================
        // HALAMAN RIWAYAT SESI
        // ========================
        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel()
            val sessions by historyViewModel.sessions.collectAsState()
            val selectedFilter by historyViewModel.selectedFilter.collectAsState()

            HistoryScreen(
                sessions = sessions,
                selectedFilter = selectedFilter,
                onFilterChange = { historyViewModel.setFilter(it) },
                onDeleteSession = { historyViewModel.deleteSession(it) },
                onStartSession = {
                    navController.navigate(Screen.Input.route)
                }
            )
        }

        // ========================
        // HALAMAN PROFIL
        // ========================
        composable(Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel()
            val userName by profileViewModel.userName.collectAsState()
            val defaultGoal by profileViewModel.defaultGoal.collectAsState()

            ProfileScreen(
                userName = userName,
                defaultGoal = defaultGoal,
                onUserNameChange = { profileViewModel.updateUserName(it) },
                onDefaultGoalChange = { profileViewModel.updateDefaultGoal(it) },
                onResetHistory = { profileViewModel.resetHistory() },
                onOpenTestMode = {
                    navController.navigate(Screen.TestMode.route)
                }
            )
        }

        // ========================
        // MODE DEBUG/TEST (Tersembunyi)
        // ========================
        composable(Screen.TestMode.route) {
            TestModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ========================
        // WORKOUT TRACKER — Tracking set/rep/berat harian
        // ========================
        composable(Screen.Tracker.route) {
            val trackerViewModel: TrackerViewModel = viewModel()
            val suggestedDay by trackerViewModel.suggestedDay.collectAsState()
            val selectedDay by trackerViewModel.selectedDay.collectAsState()
            val exercises by trackerViewModel.exercises.collectAsState()
            val isWorkoutActive by trackerViewModel.isWorkoutActive.collectAsState()
            val currentSessionId by trackerViewModel.currentSessionId.collectAsState()
            val setRecords by trackerViewModel.setRecords.collectAsState()
            val restTimerSeconds by trackerViewModel.restTimerSeconds.collectAsState()
            val isTimerRunning by trackerViewModel.isTimerRunning.collectAsState()
            val totalTimerDuration by trackerViewModel.totalTimerDuration.collectAsState()

            TrackerScreen(
                suggestedDay = suggestedDay,
                selectedDay = selectedDay,
                exercises = exercises,
                isWorkoutActive = isWorkoutActive,
                currentSessionId = currentSessionId,
                setRecords = setRecords,
                restTimerSeconds = restTimerSeconds,
                isTimerRunning = isTimerRunning,
                totalTimerDuration = totalTimerDuration,
                onSelectDay = { trackerViewModel.selectDay(it) },
                onStartWorkout = { trackerViewModel.startWorkout() },
                onEndWorkout = { trackerViewModel.endWorkout() },
                onLogSet = { exerciseId, weight, reps, intensity ->
                    trackerViewModel.logSet(exerciseId, weight, reps, intensity)
                },
                onStartTimer = { trackerViewModel.startRestTimer(it) },
                onStopTimer = { trackerViewModel.stopRestTimer() },
                onDeleteSetRecord = { trackerViewModel.deleteSetRecord(it) },
                onViewProgress = {
                    navController.navigate(Screen.Progress.route)
                }
            )
        }

        // ========================
        // PROGRESS CHART — Visualisasi progress per exercise
        // ========================
        composable(Screen.Progress.route) {
            val progressViewModel: ProgressViewModel = viewModel()
            val exercises by progressViewModel.exercises.collectAsState()
            val selectedExercise by progressViewModel.selectedExercise.collectAsState()
            val progressData by progressViewModel.progressData.collectAsState()

            ProgressScreen(
                exercises = exercises,
                selectedExercise = selectedExercise,
                progressData = progressData,
                onSelectExercise = { progressViewModel.selectExercise(it) },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
    }
}
