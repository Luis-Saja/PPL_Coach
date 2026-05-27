package com.ukdw.pplaicoach.presentation.tracker

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.ukdw.pplaicoach.data.local.ExerciseEntity
import com.ukdw.pplaicoach.data.local.ProgressPoint
import com.ukdw.pplaicoach.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * === PROGRESS SCREEN ===
 *
 * Layar untuk melihat progress latihan per exercise.
 * Fitur:
 * - Dropdown selector exercise
 * - Line chart (Vico) menampilkan max weight per sesi over time
 * - Stats ringkasan: Personal Best, total sets, avg reps
 * - Info exercise (instruksi + YouTube link)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    exercises: List<ExerciseEntity>,
    selectedExercise: ExerciseEntity?,
    progressData: List<ProgressPoint>,
    onSelectExercise: (ExerciseEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ========================
        // HEADER + BACK BUTTON
        // ========================
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Text(
                text = "📊 Progress Tracker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        }

        // ========================
        // EXERCISE SELECTOR DROPDOWN
        // ========================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pilih Gerakan:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedExercise?.name ?: "Pilih exercise...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        exercises.forEach { exercise ->
                            val icon = when (exercise.category) {
                                "PUSH" -> "💪"
                                "PULL" -> "🏋️"
                                "LEGS" -> "🦵"
                                else -> "🔥"
                            }
                            DropdownMenuItem(
                                text = { Text("$icon ${exercise.name}") },
                                onClick = {
                                    onSelectExercise(exercise)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // ========================
        // PROGRESS CHART (Vico Line Chart)
        // ========================
        if (selectedExercise != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📈 Progress: ${selectedExercise.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (progressData.size >= 2) {
                        // === CHART: Menggunakan Vico ===
                        val modelProducer = remember { CartesianChartModelProducer() }

                        LaunchedEffect(progressData) {
                            modelProducer.runTransaction {
                                lineSeries {
                                    series(progressData.map { it.weightInKg })
                                }
                            }
                        }

                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberLineCartesianLayer(),
                                startAxis = VerticalAxis.rememberStart(),
                                bottomAxis = HorizontalAxis.rememberBottom()
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )

                    } else if (progressData.size == 1) {
                        // Baru 1 data point — belum bisa chart
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LightTrainingYellowLight)
                        ) {
                            Text(
                                text = "📌 Baru 1 sesi tercatat (${progressData[0].weightInKg}kg). " +
                                        "Lakukan minimal 2 sesi untuk melihat grafik progress.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp),
                                color = OnSurfaceVariant
                            )
                        }
                    } else {
                        // Belum ada data
                        Text(
                            text = "Belum ada data progress untuk ${selectedExercise.name}.\n" +
                                    "Mulai workout dan log set untuk melihat grafik!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        )
                    }
                }
            }

            // ========================
            // STATS SUMMARY
            // ========================
            if (progressData.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NormalTrainingGreenLight)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🏆 Statistik",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NormalTrainingGreen
                        )

                        val personalBest = progressData.maxOf { it.weightInKg }
                        val totalSessions = progressData.size
                        val avgReps = progressData.map { it.reps }.average()
                        val latestWeight = progressData.last().weightInKg
                        val firstWeight = progressData.first().weightInKg
                        val improvement = latestWeight - firstWeight

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem("Personal Best", "${personalBest}kg")
                            StatItem("Total Sesi", "$totalSessions")
                            StatItem("Avg Reps", "%.1f".format(avgReps))
                        }

                        if (progressData.size >= 2) {
                            val sign = if (improvement >= 0) "+" else ""
                            Text(
                                text = "📊 Progress: $sign${improvement}kg dari sesi pertama",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = if (improvement >= 0) NormalTrainingGreen else RestDayRed
                            )
                        }
                    }
                }
            }

            // ========================
            // EXERCISE INFO CARD
            // ========================
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📖 Cara Melakukan ${selectedExercise.name}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = selectedExercise.instructions,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )

                    // Tombol YouTube
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedExercise.youtubeUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RestDayRed,
                            contentColor = OnPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.OndemandVideo, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("▶️ Tonton Video Tutorial")
                    }
                }
            }
        }

        // Spacer akhir
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/** Komponen statistik kecil */
@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = NormalTrainingGreen
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )
    }
}
