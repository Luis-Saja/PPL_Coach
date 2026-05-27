package com.ukdw.pplaicoach.presentation.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.BedtimeOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ukdw.pplaicoach.data.local.WorkoutSessionEntity
import com.ukdw.pplaicoach.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ========================
// Daftar filter yang tersedia untuk riwayat sesi
// ========================
private val filterOptions = listOf(
    "Semua",
    "Rest Day",
    "Light Training",
    "Normal Training"
)

/**
 * HistoryScreen — Layar riwayat sesi latihan.
 *
 * Menampilkan semua sesi latihan yang tersimpan di database lokal,
 * dengan fitur filter berdasarkan tipe rekomendasi dan hapus geser (swipe-to-dismiss).
 *
 * @param sessions Daftar sesi latihan yang akan ditampilkan
 * @param selectedFilter Filter yang sedang aktif ("Semua", "Rest Day", dll.)
 * @param onFilterChange Callback saat filter berubah
 * @param onDeleteSession Callback saat sesi dihapus (parameter: session id)
 * @param onStartSession Callback saat tombol mulai sesi ditekan (di empty state)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    sessions: List<WorkoutSessionEntity>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onDeleteSession: (Int) -> Unit,
    onStartSession: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // === Header Layar ===
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp)
        ) {
            Text(
                text = "Riwayat Latihan",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pantau progres latihanmu dari waktu ke waktu",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }

        // === Filter Chips (bisa di-scroll horizontal) ===
        FilterChipsRow(
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        // === Konten Utama ===
        if (sessions.isEmpty()) {
            // Tampilkan state kosong jika tidak ada sesi
            EmptyHistoryState(onStartSession = onStartSession)
        } else {
            // Tampilkan daftar sesi dalam LazyColumn
            SessionList(
                sessions = sessions,
                onDeleteSession = onDeleteSession
            )
        }
    }
}

// ========================
// Komponen: Baris Filter Chips
// ========================

/**
 * Baris chip filter yang bisa di-scroll secara horizontal.
 * Chip yang terpilih diberi warna PrimaryBlue.
 */
@Composable
private fun FilterChipsRow(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filterOptions) { filter ->
            val isSelected = filter == selectedFilter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterChange(filter) },
                label = {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = OnPrimary,
                    containerColor = SurfaceLight,
                    labelColor = OnSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = CardBorder,
                    selectedBorderColor = PrimaryBlue,
                    enabled = true,
                    selected = isSelected
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

// ========================
// Komponen: Daftar Sesi (LazyColumn)
// ========================

/**
 * LazyColumn yang menampilkan item sesi dengan fitur SwipeToDismiss.
 * Geser ke kiri untuk menghapus sesi (latar belakang merah + ikon delete).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionList(
    sessions: List<WorkoutSessionEntity>,
    onDeleteSession: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = sessions,
            key = { it.id }
        ) { session ->
            // State untuk SwipeToDismiss
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDeleteSession(session.id)
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    // Latar belakang merah saat digeser untuk menghapus
                    DismissBackground(dismissState = dismissState)
                },
                enableDismissFromStartToEnd = false,
                enableDismissFromEndToStart = true,
                content = {
                    SessionItemCard(session = session)
                }
            )
        }

        // Spacer bawah agar item terakhir tidak terpotong
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ========================
// Komponen: Latar Belakang Hapus (Swipe)
// ========================

/**
 * Latar belakang merah dengan ikon tempat sampah yang muncul
 * saat pengguna menggeser item sesi ke kiri.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            SwipeToDismissBoxValue.EndToStart -> RestDayRed
            else -> Color.Transparent
        },
        label = "dismiss_bg_color"
    )
    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0.75f,
        label = "dismiss_icon_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(color)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Hapus sesi",
            tint = OnPrimary,
            modifier = Modifier.scale(scale)
        )
    }
}

// ========================
// Komponen: Kartu Item Sesi
// ========================

/**
 * Kartu Material 3 untuk satu item sesi latihan.
 * Menampilkan tanggal, badge rekomendasi, penyesuaian beban,
 * durasi tidur, tingkat nyeri otot, dan tujuan latihan.
 */
@Composable
private fun SessionItemCard(session: WorkoutSessionEntity) {
    // Format tanggal dengan locale Indonesia
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val formattedDate = dateFormat.format(Date(session.timestamp))

    // Gaya rekomendasi berdasarkan tipe
    val (badgeColor, badgeContainerColor, badgeText) = getRecommendationStyle(session.recommendation)

    // Format dan warna penyesuaian beban
    val loadText = formatLoadAdjustment(session.loadAdjustment)
    val loadColor = getLoadColor(session.loadAdjustment)

    // Teks tujuan latihan yang ramah pengguna
    val goalText = when (session.userGoal) {
        "BULKING" -> "Bulking 💪"
        "CUTTING" -> "Cutting 🔥"
        else -> session.userGoal
    }

    // Teks tingkat nyeri otot yang ramah pengguna
    val sorenessText = when (session.muscleSoreness) {
        "LOW" -> "Rendah"
        "MEDIUM" -> "Sedang"
        "HIGH" -> "Tinggi"
        else -> session.muscleSoreness
    }
    val sorenessColor = when (session.muscleSoreness) {
        "LOW" -> NormalTrainingGreen
        "MEDIUM" -> LightTrainingYellow
        "HIGH" -> RestDayRed
        else -> NeutralGray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Baris atas: Tanggal + Badge rekomendasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = OnBackground
                )

                // Badge rekomendasi berwarna
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeContainerColor
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            // Baris: Penyesuaian beban
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Penyesuaian Beban",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Text(
                    text = loadText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = loadColor
                )
            }

            HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

            // Baris bawah: Detail kondisi (tidur, nyeri, tujuan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Durasi tidur
                DetailChip(
                    icon = Icons.Outlined.Bedtime,
                    label = "${session.sleepDuration} jam",
                    color = PrimaryBlue
                )

                // Tingkat nyeri otot
                DetailChip(
                    icon = Icons.Default.SelfImprovement,
                    label = sorenessText,
                    color = sorenessColor
                )

                // Tujuan latihan
                Text(
                    text = goalText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

/**
 * Chip kecil berisi ikon dan label untuk menampilkan detail kondisi
 * (durasi tidur, tingkat nyeri, dll.)
 */
@Composable
private fun DetailChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

// ========================
// Komponen: State Kosong (Empty State)
// ========================

/**
 * Ditampilkan saat tidak ada sesi yang tersimpan (atau hasil filter kosong).
 * Berisi ilustrasi teks, pesan motivasi, dan tombol untuk memulai latihan.
 */
@Composable
private fun EmptyHistoryState(onStartSession: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ikon ilustrasi dalam lingkaran gradient
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.15f),
                                SecondaryOrange.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "📋 Belum Ada Riwayat",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Belum ada sesi tersimpan.\nMulai latihan pertamamu!",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol CTA untuk memulai sesi baru
            Button(
                onClick = onStartSession,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryOrange,
                    contentColor = OnSecondary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mulai Latihan Sekarang",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ========================
// Fungsi Utilitas
// ========================

/**
 * Mengembalikan warna teks, warna container, dan teks badge
 * berdasarkan tipe rekomendasi dari database.
 */
private fun getRecommendationStyle(recommendation: String): Triple<Color, Color, String> {
    return when (recommendation) {
        "REST_DAY" -> Triple(RestDayRed, RestDayRedContainer, "Rest Day")
        "LIGHT_TRAINING" -> Triple(LightTrainingYellow, LightTrainingYellowContainer, "Light Training")
        "NORMAL_TRAINING" -> Triple(NormalTrainingGreen, NormalTrainingGreenContainer, "Normal Training")
        else -> Triple(NeutralGray, SurfaceVariantLight, recommendation)
    }
}

/**
 * Memformat nilai loadAdjustment menjadi string bertanda.
 * Contoh: +10 → "+10%", -20 → "-20%", 0 → "0%"
 */
private fun formatLoadAdjustment(loadAdjustment: Int): String {
    return when {
        loadAdjustment > 0 -> "+${loadAdjustment}%"
        loadAdjustment < 0 -> "${loadAdjustment}%"
        else -> "0%"
    }
}

/**
 * Mengembalikan warna sesuai nilai loadAdjustment:
 * - Positif → Hijau (pertambahan beban)
 * - Negatif → Merah (pengurangan beban)
 * - Nol    → Abu-abu (tidak berubah)
 */
private fun getLoadColor(loadAdjustment: Int): Color {
    return when {
        loadAdjustment > 0 -> PositiveGreen
        loadAdjustment < 0 -> NegativeRed
        else -> NeutralGray
    }
}

// ========================
// Preview Composable
// ========================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistoryScreenPreviewWithData() {
    val sampleSessions = listOf(
        WorkoutSessionEntity(
            id = 1,
            timestamp = System.currentTimeMillis(),
            sleepDuration = 7.5,
            muscleSoreness = "LOW",
            userGoal = "BULKING",
            prevPerformance = "STABLE",
            loadAdjustment = 10,
            volumeAdjustment = 5,
            recommendation = "NORMAL_TRAINING",
            activeRules = "[\"R4\",\"R5\"]",
            explanationText = "Latihan normal, kondisi baik."
        ),
        WorkoutSessionEntity(
            id = 2,
            timestamp = System.currentTimeMillis() - 86_400_000,
            sleepDuration = 5.0,
            muscleSoreness = "HIGH",
            userGoal = "CUTTING",
            prevPerformance = "DECREASE",
            loadAdjustment = -20,
            volumeAdjustment = -15,
            recommendation = "REST_DAY",
            activeRules = "[\"R1\",\"R2\"]",
            explanationText = "Butuh istirahat, kondisi lelah."
        ),
        WorkoutSessionEntity(
            id = 3,
            timestamp = System.currentTimeMillis() - 172_800_000,
            sleepDuration = 6.0,
            muscleSoreness = "MEDIUM",
            userGoal = "BULKING",
            prevPerformance = "INCREASE",
            loadAdjustment = 0,
            volumeAdjustment = 0,
            recommendation = "LIGHT_TRAINING",
            activeRules = "[\"R3\"]",
            explanationText = "Latihan ringan disarankan."
        )
    )

    MaterialTheme {
        HistoryScreen(
            sessions = sampleSessions,
            selectedFilter = "Semua",
            onFilterChange = {},
            onDeleteSession = {},
            onStartSession = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HistoryScreenPreviewEmpty() {
    MaterialTheme {
        HistoryScreen(
            sessions = emptyList(),
            selectedFilter = "Semua",
            onFilterChange = {},
            onDeleteSession = {},
            onStartSession = {}
        )
    }
}
