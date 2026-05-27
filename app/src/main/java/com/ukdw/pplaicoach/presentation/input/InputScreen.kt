package com.ukdw.pplaicoach.presentation.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ukdw.pplaicoach.domain.model.PrevPerformance
import com.ukdw.pplaicoach.domain.model.SorenessLevel
import com.ukdw.pplaicoach.domain.model.UserGoal
import com.ukdw.pplaicoach.domain.model.UserInput
import com.ukdw.pplaicoach.ui.theme.BackgroundLight
import com.ukdw.pplaicoach.ui.theme.CardBorder
import com.ukdw.pplaicoach.ui.theme.NegativeRed
import com.ukdw.pplaicoach.ui.theme.NormalTrainingGreen
import com.ukdw.pplaicoach.ui.theme.NormalTrainingGreenLight
import com.ukdw.pplaicoach.ui.theme.OnPrimary
import com.ukdw.pplaicoach.ui.theme.OnSurface
import com.ukdw.pplaicoach.ui.theme.OnSurfaceVariant
import com.ukdw.pplaicoach.ui.theme.PrimaryBlue
import com.ukdw.pplaicoach.ui.theme.PrimaryBlueDark
import com.ukdw.pplaicoach.ui.theme.PrimaryBlueLight
import com.ukdw.pplaicoach.ui.theme.RestDayRed
import com.ukdw.pplaicoach.ui.theme.RestDayRedLight
import com.ukdw.pplaicoach.ui.theme.SecondaryOrange
import com.ukdw.pplaicoach.ui.theme.SecondaryOrangeLight
import com.ukdw.pplaicoach.ui.theme.LightTrainingYellow
import com.ukdw.pplaicoach.ui.theme.LightTrainingYellowLight

/**
 * === INPUT SCREEN ===
 * Layar utama untuk memasukkan kondisi fisik pengguna.
 * Terdiri dari 4 variabel input: durasi tidur, nyeri otot, tujuan latihan,
 * dan performa sesi sebelumnya. Semua field wajib diisi sebelum submit.
 *
 * @param onSubmit Callback ketika data berhasil divalidasi dan dikirim
 * @param onNavigateBack Callback untuk kembali ke layar sebelumnya
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    onSubmit: (UserInput) -> Unit,
    onNavigateBack: () -> Unit
) {
    // === State untuk setiap field input ===
    var sleepDuration by remember { mutableDoubleStateOf(7.0) }
    var selectedSoreness by remember { mutableStateOf<SorenessLevel?>(null) }
    var selectedGoal by remember { mutableStateOf<UserGoal?>(null) }
    var selectedPerformance by remember { mutableStateOf<PrevPerformance?>(null) }
    var isFirstSession by remember { mutableStateOf(false) }

    // === State untuk validasi dan dialog ===
    var showValidationError by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }
    var showInconsistencyDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "📋 Input Kondisi Fisik",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryBlue,
                    titleContentColor = OnPrimary,
                    navigationIconContentColor = OnPrimary
                )
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // === Judul Halaman ===
            Text(
                text = "Masukkan kondisi fisikmu hari ini untuk mendapatkan rekomendasi latihan yang optimal.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // ====================================
            // SECTION 1: Durasi Tidur
            // ====================================
            SleepDurationSection(
                sleepDuration = sleepDuration,
                onSleepDurationChange = { sleepDuration = it }
            )

            // ====================================
            // SECTION 2: Nyeri Otot (DOMS)
            // ====================================
            MuscleSorenessSection(
                selectedSoreness = selectedSoreness,
                onSorenessSelected = { selectedSoreness = it }
            )

            // ====================================
            // SECTION 3: Tujuan Latihan
            // ====================================
            UserGoalSection(
                selectedGoal = selectedGoal,
                onGoalSelected = { selectedGoal = it }
            )

            // ====================================
            // SECTION 4: Performa Sesi Sebelumnya
            // ====================================
            PrevPerformanceSection(
                selectedPerformance = selectedPerformance,
                isFirstSession = isFirstSession,
                onPerformanceSelected = {
                    selectedPerformance = it
                    isFirstSession = false
                },
                onFirstSessionToggle = {
                    isFirstSession = true
                    // Sesi pertama = performa stabil (default aman)
                    selectedPerformance = PrevPerformance.STABLE
                }
            )

            // === Pesan validasi error ===
            AnimatedVisibility(
                visible = showValidationError,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = RestDayRedLight),
                    border = BorderStroke(1.dp, NegativeRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = NegativeRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = validationMessage,
                            color = NegativeRed,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ====================================
            // TOMBOL SUBMIT
            // ====================================
            Button(
                onClick = {
                    // Validasi: semua field harus diisi
                    when {
                        selectedSoreness == null -> {
                            validationMessage = "Pilih tingkat nyeri otot terlebih dahulu."
                            showValidationError = true
                        }
                        selectedGoal == null -> {
                            validationMessage = "Pilih tujuan latihanmu (Bulking / Cutting)."
                            showValidationError = true
                        }
                        selectedPerformance == null -> {
                            validationMessage = "Pilih performa sesi sebelumnya atau \"Sesi Pertama\"."
                            showValidationError = true
                        }
                        else -> {
                            showValidationError = false
                            // Cek inkonsistensi: performa menurun tapi nyeri rendah
                            if (selectedPerformance == PrevPerformance.DECREASE &&
                                selectedSoreness == SorenessLevel.LOW
                            ) {
                                showInconsistencyDialog = true
                            } else {
                                onSubmit(
                                    UserInput(
                                        sleepDuration = sleepDuration,
                                        muscleSoreness = selectedSoreness!!,
                                        userGoal = selectedGoal!!,
                                        prevPerformance = selectedPerformance!!
                                    )
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(PrimaryBlue, PrimaryBlueLight)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🚀 Analisis & Dapatkan Rekomendasi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // === Dialog Inkonsistensi Input ===
    if (showInconsistencyDialog) {
        InconsistencyDialog(
            onConfirm = {
                showInconsistencyDialog = false
                onSubmit(
                    UserInput(
                        sleepDuration = sleepDuration,
                        muscleSoreness = selectedSoreness!!,
                        userGoal = selectedGoal!!,
                        prevPerformance = selectedPerformance!!
                    )
                )
            },
            onDismiss = { showInconsistencyDialog = false }
        )
    }
}

// ============================================================
// COMPOSABLE: Section Durasi Tidur
// Menampilkan slider 1-12 jam dengan step 0.5 dan kategori real-time
// ============================================================
@Composable
private fun SleepDurationSection(
    sleepDuration: Double,
    onSleepDurationChange: (Double) -> Unit
) {
    // Tentukan kategori tidur berdasarkan durasi
    val sleepCategory = when {
        sleepDuration < 6.0 -> Triple("< 6 jam ⚠️", "Kurang tidur — tubuh butuh istirahat lebih", RestDayRed)
        sleepDuration <= 7.0 -> Triple("6-7 jam 😐", "Cukup tidur — bisa latihan dengan penyesuaian", LightTrainingYellow)
        else -> Triple("> 7 jam ✅", "Tidur optimal — siap latihan maksimal!", NormalTrainingGreen)
    }

    // Animasi warna latar belakang kategori
    val categoryBgColor by animateColorAsState(
        targetValue = when {
            sleepDuration < 6.0 -> RestDayRedLight
            sleepDuration <= 7.0 -> LightTrainingYellowLight
            else -> NormalTrainingGreenLight
        },
        animationSpec = tween(400),
        label = "sleepCategoryBg"
    )

    SectionCard(title = "🌙 Durasi Tidur Semalam") {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tampilkan nilai durasi tidur
            Text(
                text = "${String.format("%.1f", sleepDuration)} jam",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = sleepCategory.third
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badge kategori tidur
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(categoryBgColor)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = sleepCategory.first,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = sleepCategory.third
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = sleepCategory.second,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Slider 1–12 jam, step 0.5
            Slider(
                value = sleepDuration.toFloat(),
                onValueChange = {
                    // Bulatkan ke 0.5 terdekat
                    val rounded = (Math.round(it * 2.0f) / 2.0f).toDouble()
                    onSleepDurationChange(rounded)
                },
                valueRange = 1f..12f,
                steps = 21, // (12-1) / 0.5 - 1 = 21 steps di antara
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryBlue,
                    activeTrackColor = PrimaryBlue,
                    inactiveTrackColor = CardBorder
                )
            )

            // Label min-max
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "1 jam", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                Text(text = "12 jam", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
        }
    }
}

// ============================================================
// COMPOSABLE: Section Nyeri Otot
// Menampilkan 3 kartu seleksi (Low / Medium / High)
// ============================================================
@Composable
private fun MuscleSorenessSection(
    selectedSoreness: SorenessLevel?,
    onSorenessSelected: (SorenessLevel) -> Unit
) {
    SectionCard(title = "💪 Tingkat Nyeri Otot (DOMS)") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SorenessLevel.entries.forEach { level ->
                val isSelected = selectedSoreness == level

                // Warna dan emoji untuk setiap level
                val (emoji, borderColor, bgColor) = when (level) {
                    SorenessLevel.LOW -> Triple("😊", NormalTrainingGreen, NormalTrainingGreenLight)
                    SorenessLevel.MEDIUM -> Triple("😐", LightTrainingYellow, LightTrainingYellowLight)
                    SorenessLevel.HIGH -> Triple("😣", RestDayRed, RestDayRedLight)
                }

                Card(
                    onClick = { onSorenessSelected(level) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) bgColor else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) borderColor else CardBorder
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 4.dp else 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji indikator
                        Text(
                            text = emoji,
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = level.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) borderColor else OnSurface
                            )
                            Text(
                                text = level.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                        // Indikator terpilih
                        if (isSelected) {
                            Text(text = "✓", fontSize = 20.sp, color = borderColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// COMPOSABLE: Section Tujuan Latihan
// Toggle antara Bulking dan Cutting
// ============================================================
@Composable
private fun UserGoalSection(
    selectedGoal: UserGoal?,
    onGoalSelected: (UserGoal) -> Unit
) {
    SectionCard(title = "🎯 Tujuan Latihan") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserGoal.entries.forEach { goal ->
                val isSelected = selectedGoal == goal

                val (bgColor, borderColor) = when (goal) {
                    UserGoal.BULKING -> Pair(
                        if (isSelected) PrimaryBlue else Color.White,
                        if (isSelected) PrimaryBlueDark else CardBorder
                    )
                    UserGoal.CUTTING -> Pair(
                        if (isSelected) SecondaryOrange else Color.White,
                        if (isSelected) SecondaryOrange else CardBorder
                    )
                }

                Card(
                    onClick = { onGoalSelected(goal) },
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = borderColor
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isSelected) 6.dp else 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (goal == UserGoal.BULKING) "💪" else "🔥",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = goal.displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isSelected) OnPrimary else OnSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = goal.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) OnPrimary.copy(alpha = 0.85f) else OnSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// COMPOSABLE: Section Performa Sesi Sebelumnya
// 3 pilihan performa + opsi "Sesi Pertama"
// ============================================================
@Composable
private fun PrevPerformanceSection(
    selectedPerformance: PrevPerformance?,
    isFirstSession: Boolean,
    onPerformanceSelected: (PrevPerformance) -> Unit,
    onFirstSessionToggle: () -> Unit
) {
    SectionCard(title = "📈 Performa Sesi Sebelumnya") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // 3 opsi performa dalam satu baris
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrevPerformance.entries.forEach { perf ->
                    val isSelected = selectedPerformance == perf && !isFirstSession

                    val (emoji, chipColor) = when (perf) {
                        PrevPerformance.INCREASE -> Pair("📈", NormalTrainingGreen)
                        PrevPerformance.STABLE -> Pair("➡️", PrimaryBlue)
                        PrevPerformance.DECREASE -> Pair("📉", RestDayRed)
                    }

                    Card(
                        onClick = { onPerformanceSelected(perf) },
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) chipColor.copy(alpha = 0.12f) else Color.White
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) chipColor else CardBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = perf.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) chipColor else OnSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Tombol "Sesi Pertama"
            OutlinedButton(
                onClick = onFirstSessionToggle,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = if (isFirstSession) 2.dp else 1.dp,
                    color = if (isFirstSession) SecondaryOrange else CardBorder
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isFirstSession) SecondaryOrangeLight.copy(alpha = 0.15f) else Color.Transparent
                )
            ) {
                Text(
                    text = "🆕 Ini Sesi Pertama Saya",
                    fontWeight = if (isFirstSession) FontWeight.Bold else FontWeight.Normal,
                    color = if (isFirstSession) SecondaryOrange else OnSurfaceVariant
                )
            }
        }
    }
}

// ============================================================
// COMPOSABLE: Dialog Konfirmasi Inkonsistensi
// Muncul jika performa menurun tapi nyeri otot rendah
// ============================================================
@Composable
private fun InconsistencyDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(text = "🤔", fontSize = 40.sp)
        },
        title = {
            Text(
                text = "Data Terlihat Tidak Konsisten",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                Text(
                    text = "Kamu memilih performa \"Menurun\" tetapi tingkat nyeri otot \"Low\" (tidak sakit).",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Biasanya penurunan performa disertai dengan nyeri otot yang lebih tinggi. " +
                            "Apakah kamu yakin data ini sudah benar?",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Ya, Lanjutkan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ubah Data", color = PrimaryBlue)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

// ============================================================
// COMPOSABLE: Kartu Section — wrapper standar untuk setiap section
// ============================================================
@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}
