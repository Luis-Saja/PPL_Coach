package com.ukdw.pplaicoach.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ukdw.pplaicoach.data.local.WorkoutSessionEntity
import com.ukdw.pplaicoach.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ========================
// Daftar kutipan motivasi harian berbahasa Indonesia
// Dipilih berdasarkan hari dalam setahun (dayOfYear % jumlah kutipan)
// ========================
private val motivationalQuotes = listOf(
    "Konsistensi mengalahkan intensitas. Tetap berlatih!",
    "Tubuhmu bisa melakukan lebih dari yang pikiranmu percaya.",
    "Hasil tidak datang dalam semalam, tapi setiap repetisi mendekatkanmu.",
    "Hari ini sakit, besok kuat. Terus angkat!",
    "Tidak ada jalan pintas menuju tubuh yang kuat — hanya kerja keras.",
    "Satu langkah kecil hari ini, satu lompatan besar besok.",
    "Otot tumbuh saat kamu istirahat. Jangan abaikan recovery!",
    "Disiplin adalah jembatan antara tujuan dan pencapaian.",
    "Keringat hari ini adalah investasi untuk dirimu yang lebih baik.",
    "Jangan bandingkan dirimu dengan orang lain. Kalahkan dirimu kemarin!",
    "Setiap repetisi yang kamu lakukan adalah doa untuk tubuh yang lebih kuat.",
    "Latihan terbaik adalah latihan yang kamu lakukan secara konsisten."
)

/**
 * Mengambil kutipan motivasi berdasarkan hari dalam setahun.
 * Kutipan berubah setiap hari untuk menjaga semangat pengguna.
 */
private fun getDailyQuote(): String {
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return motivationalQuotes[dayOfYear % motivationalQuotes.size]
}

/**
 * HomeScreen — Layar utama / dashboard aplikasi PPL AI Coach.
 *
 * Menampilkan:
 * - Sapaan pengguna
 * - Tombol mulai sesi latihan
 * - Ringkasan sesi terakhir (jika ada)
 * - Statistik total sesi dan rata-rata beban mingguan
 * - Kutipan motivasi harian
 *
 * @param userName Nama pengguna untuk sapaan
 * @param lastSession Sesi latihan terakhir (null jika belum ada sesi)
 * @param totalSessions Jumlah total sesi yang telah dilakukan
 * @param weeklyAvgLoad Rata-rata penyesuaian beban mingguan (null jika belum ada data)
 * @param onStartSession Callback saat tombol mulai sesi ditekan
 * @param onViewHistory Callback saat tombol lihat riwayat ditekan
 */
@Composable
fun HomeScreen(
    userName: String,
    lastSession: WorkoutSessionEntity?,
    totalSessions: Int,
    weeklyAvgLoad: Double?,
    aiInsight: String = "",
    onStartSession: () -> Unit,
    onViewHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // === Bagian Sapaan Pengguna ===
        GreetingSection(userName = userName)

        // === Tombol Mulai Sesi Latihan (CTA Utama) ===
        StartSessionButton(onClick = onStartSession)

        // === Ringkasan Sesi Terakhir atau Selamat Datang ===
        if (lastSession != null) {
            LastSessionCard(session = lastSession)
        } else {
            WelcomeCard()
        }

        // === Baris Statistik ===
        StatsRow(
            totalSessions = totalSessions,
            weeklyAvgLoad = weeklyAvgLoad
        )

        // === AI Insight — Analisis Tren dari Sistem Pakar ===
        if (aiInsight.isNotEmpty()) {
            AIInsightCard(insight = aiInsight)
        }

        // === Kutipan Motivasi Harian ===
        MotivationalQuoteCard()

        // === Tombol Lihat Riwayat ===
        OutlinedButton(
            onClick = onViewHistory,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PrimaryBlue),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lihat Riwayat Latihan",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Spacer bawah agar konten tidak terpotong oleh navigasi
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ========================
// Komponen: Bagian Sapaan
// ========================

/**
 * Menampilkan sapaan kepada pengguna dengan emoji tangan kuat.
 */
@Composable
private fun GreetingSection(userName: String) {
    Column {
        Text(
            text = "Halo, $userName! 💪",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = OnBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Siap untuk latihan hari ini?",
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVariant
        )
    }
}

// ========================
// Komponen: Tombol Mulai Sesi
// ========================

/**
 * Tombol utama berwarna SecondaryOrange untuk memulai sesi latihan baru.
 * Desain besar dan mencolok sebagai CTA (Call To Action) utama.
 */
@Composable
private fun StartSessionButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(16.dp),
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
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Mulai Sesi Latihan Hari Ini",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ========================
// Komponen: Kartu Sesi Terakhir
// ========================

/**
 * Menampilkan ringkasan sesi latihan terakhir dalam bentuk Card.
 * Termasuk badge rekomendasi berwarna, tanggal, dan penyesuaian beban.
 */
@Composable
private fun LastSessionCard(session: WorkoutSessionEntity) {
    // Menentukan warna badge berdasarkan tipe rekomendasi
    val (badgeColor, badgeContainerColor, badgeText) = getRecommendationStyle(session.recommendation)

    // Format tanggal menggunakan locale Indonesia
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    val formattedDate = dateFormat.format(Date(session.timestamp))

    // Menentukan warna dan teks penyesuaian beban
    val loadText = formatLoadAdjustment(session.loadAdjustment)
    val loadColor = getLoadColor(session.loadAdjustment)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Judul + Badge Rekomendasi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sesi Terakhir",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )

                // Badge rekomendasi berwarna
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeContainerColor
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = badgeColor
                    )
                }
            }

            HorizontalDivider(color = DividerColor, thickness = 1.dp)

            // Tanggal sesi
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            // Penyesuaian beban
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = loadColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Penyesuaian Beban: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
                Text(
                    text = loadText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = loadColor
                )
            }
        }
    }
}

// ========================
// Komponen: Kartu Selamat Datang (Belum Ada Sesi)
// ========================

/**
 * Ditampilkan jika pengguna belum memiliki sesi latihan sama sekali.
 * Memberikan sambutan hangat dan motivasi untuk memulai latihan pertama.
 */
@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, PrimaryBlueLight.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ikon selamat datang dalam lingkaran
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlueLight)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                    contentDescription = null,
                    tint = OnPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = "Selamat Datang di AI Coach! 🎉",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlueDark,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Belum ada sesi latihan tercatat.\nTekan tombol di atas untuk memulai latihan pertamamu!",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ========================
// Komponen: Baris Statistik
// ========================

/**
 * Menampilkan dua kartu statistik berdampingan:
 * 1. Total sesi latihan
 * 2. Rata-rata penyesuaian beban mingguan
 */
@Composable
private fun StatsRow(
    totalSessions: Int,
    weeklyAvgLoad: Double?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Kartu: Total Sesi
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.FitnessCenter,
            iconTint = PrimaryBlue,
            label = "Total Sesi",
            value = totalSessions.toString(),
            containerColor = PrimaryBlue.copy(alpha = 0.08f)
        )

        // Kartu: Rata-rata Beban Mingguan
        val avgLoadText = if (weeklyAvgLoad != null) {
            val formatted = String.format(Locale.US, "%+.1f%%", weeklyAvgLoad)
            formatted
        } else {
            "—"
        }
        val avgLoadColor = when {
            weeklyAvgLoad == null -> NeutralGray
            weeklyAvgLoad > 0 -> PositiveGreen
            weeklyAvgLoad < 0 -> NegativeRed
            else -> NeutralGray
        }

        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TrendingUp,
            iconTint = avgLoadColor,
            label = "Rata-rata Beban/Minggu",
            value = avgLoadText,
            containerColor = avgLoadColor.copy(alpha = 0.08f)
        )
    }
}

/**
 * Kartu statistik individual yang menampilkan ikon, label, dan nilai.
 */
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = OnBackground
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ========================
// Komponen: Kartu AI Insight (BARU — Fitur AI)
// ========================

/**
 * Menampilkan insight cerdas dari AI berdasarkan analisis tren sesi latihan.
 * Kartu ini memberikan kesan bahwa sistem benar-benar menganalisis data pengguna.
 */
@Composable
private fun AIInsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TertiaryTeal.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, TertiaryTeal.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ikon otak AI
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(TertiaryTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧠", fontSize = 16.sp)
                }
                Column {
                    Text(
                        text = "AI Insight",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TertiaryTeal
                    )
                    Text(
                        text = "Analisis tren dari Expert System",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                lineHeight = 22.sp
            )
        }
    }
}

// ========================
// Komponen: Kartu Kutipan Motivasi
// ========================

/**
 * Menampilkan kutipan motivasi harian dalam kartu bergaya italic.
 * Kutipan berubah setiap hari berdasarkan dayOfYear.
 */
@Composable
private fun MotivationalQuoteCard() {
    val quote = getDailyQuote()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryOrange.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ikon kutipan
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = SecondaryOrange,
                modifier = Modifier.size(28.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Motivasi Hari Ini",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SecondaryOrangeDark
                )
                Text(
                    text = "\"$quote\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = OnSurfaceVariant,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

// ========================
// Fungsi Utilitas: Gaya Rekomendasi
// ========================

/**
 * Mengembalikan warna teks, warna container, dan teks badge
 * berdasarkan tipe rekomendasi.
 *
 * - REST_DAY     → Merah
 * - LIGHT_TRAINING → Kuning / Oranye
 * - NORMAL_TRAINING → Hijau
 */
private fun getRecommendationStyle(recommendation: String): Triple<Color, Color, String> {
    return when (recommendation) {
        "REST_DAY" -> Triple(RestDayRed, RestDayRedContainer, "Rest Day")
        "LIGHT_TRAINING" -> Triple(LightTrainingYellow, LightTrainingYellowContainer, "Light Training")
        "NORMAL_TRAINING" -> Triple(NormalTrainingGreen, NormalTrainingGreenContainer, "Normal Training")
        else -> Triple(NeutralGray, SurfaceVariantLight, recommendation)
    }
}

// ========================
// Fungsi Utilitas: Format Penyesuaian Beban
// ========================

/**
 * Memformat nilai loadAdjustment menjadi string bertanda (e.g., "+10%", "-20%", "0%").
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
 * - Positif → Hijau
 * - Negatif → Merah
 * - Nol    → Abu-abu
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
private fun HomeScreenPreviewWithSession() {
    val sampleSession = WorkoutSessionEntity(
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
        explanationText = "Kondisi tubuh baik, lanjutkan latihan normal."
    )

    MaterialTheme {
        HomeScreen(
            userName = "Budi",
            lastSession = sampleSession,
            totalSessions = 12,
            weeklyAvgLoad = 5.5,
            aiInsight = "📈 AI mendeteksi tren positif! Load adjustment meningkat dalam 3 sesi terakhir.",
            onStartSession = {},
            onViewHistory = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreviewEmpty() {
    MaterialTheme {
        HomeScreen(
            userName = "Budi",
            lastSession = null,
            totalSessions = 0,
            weeklyAvgLoad = null,
            aiInsight = "",
            onStartSession = {},
            onViewHistory = {}
        )
    }
}
