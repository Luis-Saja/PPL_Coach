package com.ukdw.pplaicoach.presentation.tracker

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ukdw.pplaicoach.ui.theme.*

/**
 * === REST TIMER COMPOSABLE ===
 *
 * Komponen countdown timer melingkar yang muncul setelah pengguna
 * mencatat satu set latihan. Fitur:
 * - Animasi lingkaran countdown (arc progressively shrinks)
 * - Tampilan waktu tersisa (MM:SS)
 * - Tombol preset: 60s, 90s, 120s
 * - Tombol Skip untuk melewati timer
 */
@Composable
fun RestTimerComposable(
    remainingSeconds: Int,
    totalSeconds: Int,
    isRunning: Boolean,
    onStartTimer: (Int) -> Unit,
    onStopTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animasi progress lingkaran (0.0 = penuh, 1.0 = habis)
    val progress = if (totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "timer_progress"
    )

    // Warna berubah sesuai sisa waktu
    val timerColor = when {
        remainingSeconds > 30 -> PrimaryBlue
        remainingSeconds > 10 -> LightTrainingYellow
        else -> RestDayRed
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = if (isRunning) "⏱️ Rest Timer" else "⏱️ Atur Rest Timer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )

            if (isRunning) {
                // === TIMER SEDANG BERJALAN ===

                // Lingkaran countdown animasi
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(160.dp)
                ) {
                    // Background circle (abu-abu)
                    Canvas(modifier = Modifier.size(150.dp)) {
                        drawArc(
                            color = Color(0xFFE0E0E0),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = StrokeCap.Round),
                            topLeft = Offset(6f, 6f),
                            size = Size(size.width - 12f, size.height - 12f)
                        )
                    }

                    // Progress arc (berwarna)
                    Canvas(modifier = Modifier.size(150.dp)) {
                        drawArc(
                            color = timerColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 12f, cap = StrokeCap.Round),
                            topLeft = Offset(6f, 6f),
                            size = Size(size.width - 12f, size.height - 12f)
                        )
                    }

                    // Teks waktu di tengah lingkaran
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatTime(remainingSeconds),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = timerColor,
                            fontSize = 36.sp
                        )
                        Text(
                            text = "tersisa",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Tombol Skip
                OutlinedButton(
                    onClick = onStopTimer,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RestDayRed)
                ) {
                    Text("⏭️ Lewati Timer")
                }

            } else {
                // === TIMER TIDAK AKTIF — Tampilkan preset buttons ===

                Text(
                    text = "Pilih durasi istirahat:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Preset: 60 detik
                    FilledTonalButton(
                        onClick = { onStartTimer(60) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PrimaryBlue.copy(alpha = 0.1f),
                            contentColor = PrimaryBlue
                        )
                    ) {
                        Text("60s", fontWeight = FontWeight.Bold)
                    }

                    // Preset: 90 detik (default)
                    Button(
                        onClick = { onStartTimer(90) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = OnPrimary
                        )
                    ) {
                        Text("90s", fontWeight = FontWeight.Bold)
                    }

                    // Preset: 120 detik
                    FilledTonalButton(
                        onClick = { onStartTimer(120) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PrimaryBlue.copy(alpha = 0.1f),
                            contentColor = PrimaryBlue
                        )
                    ) {
                        Text("120s", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/** Format detik menjadi MM:SS */
private fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return "%d:%02d".format(min, sec)
}
