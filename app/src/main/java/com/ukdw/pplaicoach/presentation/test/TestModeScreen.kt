package com.ukdw.pplaicoach.presentation.test

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ukdw.pplaicoach.domain.model.*
import com.ukdw.pplaicoach.domain.rules.InferenceEngine
import com.ukdw.pplaicoach.ui.theme.*

/**
 * === TEST MODE SCREEN (ENHANCED) ===
 *
 * Layar debug/validasi untuk keperluan akademik.
 * Menjalankan semua 5 skenario uji (T1-T5) dari proposal dan menampilkan hasil
 * lengkap dengan confidence score, inference trace, dan waktu pemrosesan.
 *
 * Fitur baru:
 * - AI Engine Info card menampilkan arsitektur sistem pakar
 * - Confidence score untuk setiap skenario
 * - Waktu pemrosesan (processing time) per skenario
 * - Statistik rules evaluated/matched/fired
 */

// ========================
// Data class untuk skenario test
// ========================
private data class TestScenario(
    val id: String,
    val name: String,
    val input: UserInput,
    val expectedLoad: Int,
    val expectedVolume: Int,
    val expectedRecommendation: RecommendationType,
    val expectedRules: List<String>
)

// Data class untuk hasil test
private data class TestResult(
    val scenario: TestScenario,
    val actualResult: InferenceResult,
    val passed: Boolean,
    val loadMatch: Boolean,
    val volumeMatch: Boolean,
    val recMatch: Boolean,
    val rulesMatch: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestModeScreen(onNavigateBack: () -> Unit) {
    // Definisi 5 skenario uji dari proposal
    val scenarios = remember {
        listOf(
            TestScenario(
                id = "T1", name = "Recovery Kritis",
                input = UserInput(5.0, SorenessLevel.HIGH, UserGoal.BULKING, PrevPerformance.STABLE),
                expectedLoad = -20, expectedVolume = 0,
                expectedRecommendation = RecommendationType.REST_DAY,
                expectedRules = listOf("R1")
            ),
            TestScenario(
                id = "T2", name = "Multi-Rule Cutting",
                input = UserInput(5.0, SorenessLevel.MEDIUM, UserGoal.CUTTING, PrevPerformance.STABLE),
                expectedLoad = -15, expectedVolume = -15,
                expectedRecommendation = RecommendationType.LIGHT_TRAINING,
                expectedRules = listOf("R2", "R5", "R6")
            ),
            TestScenario(
                id = "T3", name = "Kondisi Optimal Bulking",
                input = UserInput(8.0, SorenessLevel.LOW, UserGoal.BULKING, PrevPerformance.STABLE),
                expectedLoad = 10, expectedVolume = 0,
                expectedRecommendation = RecommendationType.NORMAL_TRAINING,
                expectedRules = listOf("R3", "R4")
            ),
            TestScenario(
                id = "T4", name = "Cutting Volume Control",
                input = UserInput(7.0, SorenessLevel.MEDIUM, UserGoal.CUTTING, PrevPerformance.STABLE),
                expectedLoad = 0, expectedVolume = -15,
                expectedRecommendation = RecommendationType.NORMAL_TRAINING,
                expectedRules = listOf("R5")
            ),
            TestScenario(
                id = "T5", name = "Progressive Overload",
                input = UserInput(6.0, SorenessLevel.LOW, UserGoal.BULKING, PrevPerformance.STABLE),
                expectedLoad = 5, expectedVolume = 0,
                expectedRecommendation = RecommendationType.NORMAL_TRAINING,
                expectedRules = listOf("R4")
            )
        )
    }

    val engine = remember { InferenceEngine() }
    var testResults by remember { mutableStateOf<List<TestResult>>(emptyList()) }
    var hasRun by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🧪 Mode Debug",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Forward Chaining Expert System Validator",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ========================
            // Kartu Info AI Engine
            // ========================
            item {
                AIEngineInfoCard()
            }

            // ========================
            // Tombol Run Semua Test
            // ========================
            item {
                Button(
                    onClick = {
                        testResults = scenarios.map { scenario ->
                            val result = engine.run(scenario.input)
                            val loadMatch = result.loadAdjustment == scenario.expectedLoad
                            val volMatch = result.volumeAdjustment == scenario.expectedVolume
                            val recMatch = result.recommendation == scenario.expectedRecommendation
                            val rulesMatch = result.activeRules == scenario.expectedRules

                            TestResult(
                                scenario = scenario,
                                actualResult = result,
                                passed = loadMatch && volMatch && recMatch && rulesMatch,
                                loadMatch = loadMatch,
                                volumeMatch = volMatch,
                                recMatch = recMatch,
                                rulesMatch = rulesMatch
                            )
                        }
                        hasRun = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, null, Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "▶ Run Semua Test (${scenarios.size} Skenario)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ========================
            // Ringkasan Hasil (jika sudah dijalankan)
            // ========================
            if (hasRun && testResults.isNotEmpty()) {
                item {
                    val passedCount = testResults.count { it.passed }
                    val allPassed = passedCount == testResults.size

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (allPassed)
                                NormalTrainingGreen.copy(alpha = 0.1f)
                            else
                                RestDayRed.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (allPassed) NormalTrainingGreen else RestDayRed
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (allPassed) "✅" else "❌",
                                fontSize = 28.sp
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (allPassed) "All Tests Passed" else "${testResults.size - passedCount} Tests Failed",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (allPassed) NormalTrainingGreen else RestDayRed
                                )
                                Text(
                                    text = "$passedCount/${testResults.size} skenario berhasil divalidasi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // ========================
            // Kartu detail setiap skenario test
            // ========================
            if (hasRun) {
                itemsIndexed(testResults) { index, testResult ->
                    TestScenarioCard(
                        index = index,
                        testResult = testResult
                    )
                }
            }
        }
    }
}

// ========================
// Kartu Info AI Engine — Arsitektur Sistem Pakar
// ========================
@Composable
private fun AIEngineInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue.copy(alpha = 0.06f)
        ),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(PrimaryBlue, TertiaryTeal))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Engine Architecture",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlueDark
                    )
                    Text(
                        text = "Rule-Based Expert System",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = PrimaryBlue.copy(alpha = 0.15f))

            // Spesifikasi AI
            val specs = listOf(
                "📚" to "Knowledge Base: 6 Production Rules",
                "🔗" to "Metode Inferensi: Forward Chaining",
                "⚖️" to "Conflict Resolution: Priority-Based Ordering",
                "🧠" to "Tipe: Deterministic Rule-Based Expert System",
                "📊" to "Variabel Input: 4 (Sleep, Soreness, Goal, Performance)",
                "🎯" to "Output: Recommendation + Load% + Volume% + Confidence"
            )

            specs.forEach { (icon, text) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(icon, fontSize = 14.sp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurface,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// ========================
// Kartu Skenario Test Individual
// ========================
@Composable
private fun TestScenarioCard(index: Int, testResult: TestResult) {
    val scenario = testResult.scenario
    val result = testResult.actualResult
    val passed = testResult.passed

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = BorderStroke(
            1.dp,
            if (passed) NormalTrainingGreen.copy(alpha = 0.4f) else RestDayRed.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: ID + Nama + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge nomor skenario
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (passed) NormalTrainingGreen.copy(alpha = 0.15f)
                                else RestDayRed.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = scenario.id,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (passed) NormalTrainingGreen else RestDayRed
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = scenario.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        // Input summary
                        Text(
                            text = "💤${scenario.input.sleepDuration}h  💪${scenario.input.muscleSoreness.displayName}  🎯${scenario.input.userGoal.name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                // PASS / FAIL badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (passed) NormalTrainingGreen.copy(alpha = 0.15f)
                    else RestDayRed.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (passed) "✅ PASS" else "❌ FAIL",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (passed) NormalTrainingGreen else RestDayRed
                    )
                }
            }

            HorizontalDivider(color = DividerColor)

            // Tabel perbandingan Expected vs Actual
            ComparisonRow("Load", "${scenario.expectedLoad}%", "${result.loadAdjustment}%", testResult.loadMatch)
            ComparisonRow("Volume", "${scenario.expectedVolume}%", "${result.volumeAdjustment}%", testResult.volumeMatch)
            ComparisonRow("Rec", scenario.expectedRecommendation.displayName, result.recommendation.displayName, testResult.recMatch)
            ComparisonRow("Rules", scenario.expectedRules.joinToString(","), result.activeRules.joinToString(","), testResult.rulesMatch)

            HorizontalDivider(color = DividerColor)

            // Statistik AI (Enhanced)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AIStatChip(
                    icon = "🧠",
                    label = "Confidence",
                    value = "${String.format("%.0f", result.confidenceScore.value * 100)}%"
                )
                AIStatChip(
                    icon = "⏱",
                    label = "Waktu",
                    value = "${result.inferenceTrace.processingTimeMs}ms"
                )
                AIStatChip(
                    icon = "🔍",
                    label = "Rules",
                    value = "${result.inferenceTrace.totalRulesMatched}/${result.inferenceTrace.totalRulesInKB}"
                )
                AIStatChip(
                    icon = "🔥",
                    label = "Fired",
                    value = "${result.inferenceTrace.totalRulesFired}"
                )
            }
        }
    }
}

// ========================
// Baris Perbandingan Expected vs Actual
// ========================
@Composable
private fun ComparisonRow(label: String, expected: String, actual: String, match: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            modifier = Modifier.width(60.dp),
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = expected,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = if (match) "=" else "≠",
            style = MaterialTheme.typography.labelSmall,
            color = if (match) NormalTrainingGreen else RestDayRed,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = actual,
            style = MaterialTheme.typography.labelSmall,
            color = if (match) NormalTrainingGreen else RestDayRed,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = if (match) "✓" else "✗",
            style = MaterialTheme.typography.labelSmall,
            color = if (match) NormalTrainingGreen else RestDayRed,
            fontWeight = FontWeight.Bold
        )
    }
}

// ========================
// Chip Statistik AI
// ========================
@Composable
private fun AIStatChip(icon: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(icon, fontSize = 14.sp)
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            fontSize = 9.sp
        )
    }
}
