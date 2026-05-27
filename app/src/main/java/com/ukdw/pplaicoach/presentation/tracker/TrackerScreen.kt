package com.ukdw.pplaicoach.presentation.tracker

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ukdw.pplaicoach.data.local.*
import com.ukdw.pplaicoach.ui.theme.*

/**
 * === TRACKER SCREEN ===
 *
 * Layar utama Workout Tracker. Fitur:
 * 1. Auto-suggest hari PPL dengan opsi override
 * 2. Daftar exercise untuk hari yang dipilih + instruksi + YouTube link
 * 3. Form input set (berat, reps, intensitas)
 * 4. Rest timer otomatis setelah log set
 * 5. Daftar set yang sudah dicatat per sesi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerScreen(
    suggestedDay: ExerciseCategory,
    selectedDay: ExerciseCategory,
    exercises: List<ExerciseEntity>,
    isWorkoutActive: Boolean,
    currentSessionId: Long?,
    setRecords: List<SetRecordEntity>,
    restTimerSeconds: Int,
    isTimerRunning: Boolean,
    totalTimerDuration: Int,
    onSelectDay: (ExerciseCategory) -> Unit,
    onStartWorkout: () -> Unit,
    onEndWorkout: () -> Unit,
    onLogSet: (exerciseId: Int, weight: Double, reps: Int, intensity: IntensityType) -> Unit,
    onStartTimer: (Int) -> Unit,
    onStopTimer: () -> Unit,
    onDeleteSetRecord: (Long) -> Unit,
    onViewProgress: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ========================
        // HEADER
        // ========================
        item {
            Text(
                text = "🏋️ Workout Tracker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }

        // ========================
        // SUGGESTED DAY CARD + PPL SELECTOR
        // ========================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Suggest: ${suggestedDay.displayName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlue
                        )
                    }

                    Text(
                        text = "Pilih hari latihan:",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )

                    // Tombol pilihan Push / Pull / Legs
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExerciseCategory.entries.forEachIndexed { index, category ->
                            SegmentedButton(
                                selected = selectedDay == category,
                                onClick = { onSelectDay(category) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = ExerciseCategory.entries.size
                                ),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = PrimaryBlue,
                                    activeContentColor = OnPrimary
                                ),
                                enabled = !isWorkoutActive // Disable saat workout aktif
                            ) {
                                Text(text = category.name)
                            }
                        }
                    }
                }
            }
        }

        // ========================
        // TOMBOL START/END WORKOUT
        // ========================
        item {
            if (!isWorkoutActive) {
                Button(
                    onClick = onStartWorkout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NormalTrainingGreen,
                        contentColor = OnPrimary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mulai Workout ${selectedDay.name}", fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = onEndWorkout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RestDayRed)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Selesai Workout", fontWeight = FontWeight.Bold)
                }
            }
        }

        // ========================
        // REST TIMER (muncul saat workout aktif)
        // ========================
        if (isWorkoutActive && (isTimerRunning || setRecords.isNotEmpty())) {
            item {
                RestTimerComposable(
                    remainingSeconds = restTimerSeconds,
                    totalSeconds = totalTimerDuration,
                    isRunning = isTimerRunning,
                    onStartTimer = onStartTimer,
                    onStopTimer = onStopTimer
                )
            }
        }

        // ========================
        // DAFTAR EXERCISE + FORM INPUT SET
        // ========================
        item {
            Text(
                text = "📋 Gerakan ${selectedDay.displayName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )
        }

        items(exercises, key = { it.id }) { exercise ->
            ExerciseCard(
                exercise = exercise,
                isWorkoutActive = isWorkoutActive,
                setRecords = setRecords.filter { it.exerciseId == exercise.id },
                onLogSet = { weight, reps, intensity ->
                    onLogSet(exercise.id, weight, reps, intensity)
                },
                onDeleteSetRecord = onDeleteSetRecord
            )
        }

        // ========================
        // TOMBOL LIHAT PROGRESS
        // ========================
        item {
            OutlinedButton(
                onClick = onViewProgress,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.BarChart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lihat Progress Chart")
            }
        }

        // Spacer akhir
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

/**
 * Kartu satu exercise dengan:
 * - Nama + kategori
 * - Expandable: instruksi langkah-demi-langkah
 * - Tombol YouTube
 * - Form input set (jika workout aktif)
 * - Daftar set yang sudah dicatat
 */
@Composable
private fun ExerciseCard(
    exercise: ExerciseEntity,
    isWorkoutActive: Boolean,
    setRecords: List<SetRecordEntity>,
    onLogSet: (weight: Double, reps: Int, intensity: IntensityType) -> Unit,
    onDeleteSetRecord: (Long) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(isWorkoutActive) }
    var showInstructions by remember { mutableStateOf(false) }

    // Form state
    var weightText by remember { mutableStateOf("") }
    var repsText by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableStateOf(IntensityType.NORMAL) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Nama exercise + tombol aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ikon kategori
                val categoryIcon = when (exercise.category) {
                    "PUSH" -> "💪"
                    "PULL" -> "🏋️"
                    "LEGS" -> "🦵"
                    else -> "🔥"
                }

                Text(
                    text = "$categoryIcon ${exercise.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Tombol info (instruksi)
                IconButton(onClick = { showInstructions = !showInstructions }) {
                    Icon(
                        imageVector = if (showInstructions) Icons.Default.ExpandLess else Icons.Default.Info,
                        contentDescription = "Instruksi",
                        tint = PrimaryBlue
                    )
                }

                // Tombol YouTube
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercise.youtubeUrl))
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.Default.OndemandVideo,
                        contentDescription = "Video Tutorial",
                        tint = RestDayRed
                    )
                }

                // Tombol expand/collapse (untuk form input)
                if (isWorkoutActive) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand"
                        )
                    }
                }
            }

            // Instruksi (expandable)
            AnimatedVisibility(visible = showInstructions) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.05f))
                ) {
                    Text(
                        text = exercise.instructions,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = OnSurfaceVariant
                    )
                }
            }

            // Form input set (hanya saat workout aktif dan card expanded)
            AnimatedVisibility(visible = isWorkoutActive && expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = DividerColor)

                    // Row 1: Weight + Reps
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            label = { Text("Berat (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue
                            )
                        )

                        OutlinedTextField(
                            value = repsText,
                            onValueChange = { repsText = it },
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue
                            )
                        )
                    }

                    // Row 2: Intensity type chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IntensityType.entries.forEach { intensity ->
                            FilterChip(
                                selected = selectedIntensity == intensity,
                                onClick = { selectedIntensity = intensity },
                                label = { Text(intensity.displayName, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = OnPrimary
                                )
                            )
                        }
                    }

                    // Tombol Log Set
                    Button(
                        onClick = {
                            val weight = weightText.toDoubleOrNull()
                            val reps = repsText.toIntOrNull()
                            if (weight != null && reps != null && weight > 0 && reps > 0) {
                                onLogSet(weight, reps, selectedIntensity)
                                weightText = ""
                                repsText = ""
                                selectedIntensity = IntensityType.NORMAL
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = weightText.isNotBlank() && repsText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondaryOrange,
                            contentColor = OnSecondary
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Set ✓", fontWeight = FontWeight.Bold)
                    }

                    // Daftar set yang sudah dicatat untuk exercise ini
                    if (setRecords.isNotEmpty()) {
                        Text(
                            text = "Set tercatat: ${setRecords.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )

                        setRecords.forEachIndexed { index, record ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Set ${index + 1}: ${record.weightInKg}kg × ${record.reps} rep",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )

                                // Badge intensitas
                                if (record.intensityType != IntensityType.NORMAL.name) {
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                text = record.intensityType.replace("_", " "),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = SecondaryOrange.copy(alpha = 0.1f),
                                            labelColor = SecondaryOrange
                                        )
                                    )
                                }

                                // Tombol hapus
                                IconButton(
                                    onClick = { onDeleteSetRecord(record.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Hapus",
                                        tint = NeutralGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
