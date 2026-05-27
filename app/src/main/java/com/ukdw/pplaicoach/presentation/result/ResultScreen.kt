package com.ukdw.pplaicoach.presentation.result

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ukdw.pplaicoach.domain.model.*
import com.ukdw.pplaicoach.ui.theme.*
import kotlinx.coroutines.delay

/**
 * === RESULT SCREEN (ENHANCED — Full AI Visualization) ===
 *
 * Layar hasil inferensi dengan visualisasi proses AI yang komprehensif:
 * - Multi-step loading animation (6 fase Forward Chaining)
 * - Confidence Score meter dengan animasi
 * - Knowledge Base evaluation (semua 6 rules ditampilkan)
 * - Forward Chaining trace timeline
 * - Explainability section
 *
 * Layar ini dirancang untuk meyakinkan bahwa aplikasi benar-benar
 * menggunakan AI (Expert System) dalam proses pengambilan keputusan.
 */

@Composable
fun ResultScreen(
    inferenceResult: InferenceResult?,
    userInput: UserInput?,
    isSaved: Boolean,
    isLoading: Boolean,
    showDecreaseWarning: Boolean,
    currentAIStep: String,
    currentStepIndex: Int,
    totalSteps: Int,
    onSave: () -> Unit,
    onReInput: () -> Unit,
    onViewHistory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        if (isLoading) {
            // ========================
            // MULTI-STEP AI LOADING ANIMATION
            // ========================
            AILoadingView(
                currentStep = currentAIStep,
                currentStepIndex = currentStepIndex,
                totalSteps = totalSteps
            )
        } else if (inferenceResult != null && userInput != null) {
            // ========================
            // HASIL INFERENSI
            // ========================
            ResultContentView(
                result = inferenceResult,
                input = userInput,
                isSaved = isSaved,
                showDecreaseWarning = showDecreaseWarning,
                onSave = onSave,
                onReInput = onReInput,
                onViewHistory = onViewHistory
            )
        } else {
            // ========================
            // EMPTY STATE
            // ========================
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧠", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Belum ada hasil inferensi",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

// ================================================================
// MULTI-STEP AI LOADING VIEW
// ================================================================
@Composable
private fun AILoadingView(
    currentStep: String,
    currentStepIndex: Int,
    totalSteps: Int
) {
    // Animasi pulsing untuk ikon otak
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val progress = if (totalSteps > 0) currentStepIndex.toFloat() / totalSteps else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )

    // Daftar langkah AI untuk ditampilkan
    val steps = listOf(
        "🧠 Inisialisasi Working Memory",
        "📊 Memuat fakta input",
        "🔍 Evaluasi 6 Production Rules",
        "⚖️ Conflict Resolution",
        "🔥 Eksekusi Forward Chaining",
        "✅ Hitung Confidence Score"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ikon otak dengan efek pulsing dan glow
        Box(contentAlignment = Alignment.Center) {
            // Glow circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale * 1.2f)
                    .alpha(glowAlpha * 0.5f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Main brain circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, TertiaryTeal)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🧠", fontSize = 36.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "AI Sedang Menganalisis...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlueDark
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Forward Chaining Expert System",
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = PrimaryBlue,
            trackColor = PrimaryBlue.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${currentStepIndex}/$totalSteps langkah",
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Daftar langkah dengan status
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                steps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Status icon
                        when {
                            index < currentStepIndex -> {
                                // Completed
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(NormalTrainingGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            index == currentStepIndex -> {
                                // Current — spinning
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = PrimaryBlue
                                )
                            }
                            else -> {
                                // Future — grayed out
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(DisabledColor.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${index + 1}",
                                        fontSize = 10.sp,
                                        color = OnSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                index < currentStepIndex -> NormalTrainingGreen
                                index == currentStepIndex -> PrimaryBlueDark
                                else -> OnSurfaceVariant.copy(alpha = 0.5f)
                            },
                            fontWeight = if (index == currentStepIndex) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// ================================================================
// RESULT CONTENT VIEW — Menampilkan semua hasil inferensi
// ================================================================
@Composable
private fun ResultContentView(
    result: InferenceResult,
    input: UserInput,
    isSaved: Boolean,
    showDecreaseWarning: Boolean,
    onSave: () -> Unit,
    onReInput: () -> Unit,
    onViewHistory: () -> Unit
) {
    // Animasi stagger untuk setiap section
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ========================
        // 1. CONFIDENCE SCORE METER
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 30 }
        ) {
            ConfidenceScoreCard(confidence = result.confidenceScore)
        }

        // ========================
        // 2. AI STATISTICS BAR
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(tween(400, delayMillis = 100)) { 30 }
        ) {
            AIStatsBar(trace = result.inferenceTrace)
        }

        // ========================
        // 3. MAIN RECOMMENDATION
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(tween(400, delayMillis = 200)) { 30 }
        ) {
            RecommendationCard(recommendation = result.recommendation)
        }

        // ========================
        // 4. ADJUSTMENT PANEL
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(tween(400, delayMillis = 300)) { 30 }
        ) {
            AdjustmentPanel(
                loadAdjustment = result.loadAdjustment,
                volumeAdjustment = result.volumeAdjustment
            )
        }

        // ========================
        // 5. KNOWLEDGE BASE EVALUATION (menunjukkan semua 6 rules)
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 400)) + slideInVertically(tween(400, delayMillis = 400)) { 30 }
        ) {
            KnowledgeBaseSection(evaluations = result.inferenceTrace.ruleEvaluations)
        }

        // ========================
        // 6. FORWARD CHAINING TRACE (collapsible)
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 500)) + slideInVertically(tween(400, delayMillis = 500)) { 30 }
        ) {
            ForwardChainingTraceSection(steps = result.inferenceTrace.steps)
        }

        // ========================
        // 7. EXPLAINABILITY SECTION
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 600)) + slideInVertically(tween(400, delayMillis = 600)) { 30 }
        ) {
            ExplainabilitySection(
                activeRules = result.activeRules,
                explanations = result.explanations
            )
        }

        // ========================
        // 8. CONTEXTUAL MESSAGE
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 700)) + slideInVertically(tween(400, delayMillis = 700)) { 30 }
        ) {
            ContextualMessageCard(message = result.contextualMessage)
        }

        // ========================
        // 9. PERFORMANCE DECREASE WARNING
        // ========================
        if (showDecreaseWarning) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(400, delayMillis = 800))
            ) {
                PerformanceWarningCard()
            }
        }

        // ========================
        // 10. INPUT SUMMARY
        // ========================
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400, delayMillis = 800))
        ) {
            InputSummaryCard(input = input)
        }

        // ========================
        // 11. PERMANENT DISCLAIMER
        // ========================
        DisclaimerCard()

        // ========================
        // 12. ACTION BUTTONS
        // ========================
        ActionButtons(
            isSaved = isSaved,
            onSave = onSave,
            onReInput = onReInput,
            onViewHistory = onViewHistory
        )

        Spacer(Modifier.height(16.dp))
    }
}

// ================================================================
// CONFIDENCE SCORE CARD
// ================================================================
@Composable
private fun ConfidenceScoreCard(confidence: ConfidenceScore) {
    val animatedValue by animateFloatAsState(
        targetValue = confidence.value.toFloat(),
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "confidence"
    )

    val confidenceColor = when {
        confidence.value >= 0.85 -> NormalTrainingGreen
        confidence.value >= 0.70 -> TertiaryTeal
        confidence.value >= 0.55 -> LightTrainingYellow
        else -> RestDayRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🧠 AI Confidence Score",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlueDark
            )

            Spacer(Modifier.height(16.dp))

            // Circular progress dengan persentase di tengah
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animatedValue },
                    modifier = Modifier.size(100.dp),
                    strokeWidth = 8.dp,
                    color = confidenceColor,
                    trackColor = confidenceColor.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedValue * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = confidenceColor
                    )
                    Text(
                        text = confidence.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = confidence.reasoning,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

// ================================================================
// AI STATISTICS BAR
// ================================================================
@Composable
private fun AIStatsBar(trace: InferenceTrace) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AIStatItem("🔍", "${trace.totalRulesEvaluated}", "Dievaluasi")
        AIStatItem("✅", "${trace.totalRulesMatched}", "Cocok")
        AIStatItem("🔥", "${trace.totalRulesFired}", "Dieksekusi")
        AIStatItem("⏱", "${trace.processingTimeMs}ms", "Proses")
    }
}

@Composable
private fun AIStatItem(icon: String, value: String, label: String) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantLight)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 16.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
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
}

// ================================================================
// MAIN RECOMMENDATION CARD
// ================================================================
@Composable
private fun RecommendationCard(recommendation: RecommendationType) {
    val (emoji, title, description, bgColor, borderColor) = when (recommendation) {
        RecommendationType.REST_DAY -> listOf(
            "😴", "Rest Day",
            "Istirahat total untuk pemulihan tubuh",
            RestDayRedLight, RestDayRed
        )
        RecommendationType.LIGHT_TRAINING -> listOf(
            "🔥", "Light Training",
            "Latihan ringan dengan intensitas rendah",
            LightTrainingYellowLight, LightTrainingYellow
        )
        RecommendationType.NORMAL_TRAINING -> listOf(
            "💪", "Normal Training",
            "Latihan normal sesuai program PPL",
            NormalTrainingGreenLight, NormalTrainingGreen
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor as Color),
        border = BorderStroke(2.dp, borderColor as Color),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon besar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(borderColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji as String, fontSize = 28.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = "Rekomendasi AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = title as String,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = borderColor
                )
                Text(
                    text = description as String,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

// ================================================================
// ADJUSTMENT PANEL
// ================================================================
@Composable
private fun AdjustmentPanel(loadAdjustment: Int, volumeAdjustment: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Load badge
        AdjustmentBadge(
            modifier = Modifier.weight(1f),
            icon = "📦",
            label = "Beban",
            value = loadAdjustment,
            alwaysShow = true
        )

        // Volume badge (hanya tampil jika != 0)
        if (volumeAdjustment != 0) {
            AdjustmentBadge(
                modifier = Modifier.weight(1f),
                icon = "📊",
                label = "Volume",
                value = volumeAdjustment,
                alwaysShow = false
            )
        }
    }
}

@Composable
private fun AdjustmentBadge(
    modifier: Modifier,
    icon: String,
    label: String,
    value: Int,
    alwaysShow: Boolean
) {
    val color = when {
        value > 0 -> PositiveGreen
        value < 0 -> NegativeRed
        else -> NeutralGray
    }
    val valueText = when {
        value > 0 -> "+$value%"
        value < 0 -> "$value%"
        else -> "0%"
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

// ================================================================
// KNOWLEDGE BASE EVALUATION (menampilkan semua 6 rules)
// ================================================================
@Composable
private fun KnowledgeBaseSection(evaluations: List<RuleEvaluation>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📋", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Knowledge Base — Evaluasi Rules",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlueDark
                    )
                    Text(
                        text = "${evaluations.count { it.isMatched }}/${evaluations.size} rules cocok",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            evaluations.forEach { eval ->
                RuleEvaluationRow(eval = eval)
                if (eval != evaluations.last()) {
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun RuleEvaluationRow(eval: RuleEvaluation) {
    val bgColor = if (eval.isMatched)
        NormalTrainingGreen.copy(alpha = 0.06f)
    else
        SurfaceVariantLight

    val borderColor = if (eval.isMatched)
        NormalTrainingGreen.copy(alpha = 0.3f)
    else
        CardBorder.copy(alpha = 0.5f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .alpha(if (eval.isMatched) 1f else 0.55f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            Text(
                text = if (eval.isMatched) "✅" else "❌",
                fontSize = 14.sp
            )
            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = eval.ruleId,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (eval.isMatched) NormalTrainingGreen else OnSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = eval.ruleName,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
                Text(
                    text = eval.condition,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = OnSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            // Priority badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (eval.priority == 1)
                    SecondaryOrange.copy(alpha = 0.1f)
                else
                    PrimaryBlue.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "P${eval.priority}",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = if (eval.priority == 1) SecondaryOrange else PrimaryBlue
                )
            }
        }
    }
}

// ================================================================
// FORWARD CHAINING TRACE (expandable timeline)
// ================================================================
@Composable
private fun ForwardChainingTraceSection(steps: List<InferenceStep>) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header (clickable untuk expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔗", fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Jejak Forward Chaining",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlueDark
                        )
                        Text(
                            text = "${steps.size} fase inferensi",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.ExpandLess
                    else
                        Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = OnSurfaceVariant
                )
            }

            // Expandable content — timeline
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(),
                exit = shrinkVertically(tween(300)) + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    steps.forEachIndexed { index, step ->
                        TraceTimelineItem(
                            step = step,
                            isLast = index == steps.lastIndex
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TraceTimelineItem(step: InferenceStep, isLast: Boolean) {
    var showDetails by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline column (node + line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            // Node icon
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(step.phase.icon, fontSize = 12.sp)
            }
            // Connecting line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(PrimaryBlue.copy(alpha = 0.2f))
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { if (step.details.isNotEmpty()) showDetails = !showDetails }
                .padding(bottom = if (isLast) 0.dp else 8.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )
            Text(
                text = step.description,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                lineHeight = 16.sp,
                maxLines = if (showDetails) Int.MAX_VALUE else 2
            )

            // Expandable details
            AnimatedVisibility(visible = showDetails && step.details.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    step.details.forEach { detail ->
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = OnSurfaceVariant,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

// ================================================================
// EXPLAINABILITY SECTION
// ================================================================
@Composable
private fun ExplainabilitySection(
    activeRules: List<String>,
    explanations: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Penjelasan Keputusan AI",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlueDark
                )
            }

            Spacer(Modifier.height(10.dp))

            if (explanations.isEmpty()) {
                Text(
                    text = "Kondisi tubuhmu optimal! Tidak ada rule spesifik yang aktif. " +
                            "Lanjutkan latihan normal sesuai program.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            } else {
                explanations.forEachIndexed { index, explanation ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = NormalTrainingGreen.copy(alpha = 0.05f),
                        border = BorderStroke(1.dp, NormalTrainingGreen.copy(alpha = 0.15f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp)) {
                            Text("✅", fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    if (index < explanations.lastIndex) {
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

// ================================================================
// CONTEXTUAL MESSAGE
// ================================================================
@Composable
private fun ContextualMessageCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = TertiaryTeal.copy(alpha = 0.06f)
        ),
        border = BorderStroke(1.dp, TertiaryTeal.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Text("💬", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
                lineHeight = 22.sp
            )
        }
    }
}

// ================================================================
// PERFORMANCE DECREASE WARNING
// ================================================================
@Composable
private fun PerformanceWarningCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RestDayRedLight),
        border = BorderStroke(1.dp, RestDayRed.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Text("⚠️", fontSize = 18.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = "Performa Menurun Terdeteksi",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = RestDayRed
                )
                Text(
                    text = "Kamu melaporkan penurunan performa. Evaluasi faktor istirahat, nutrisi, dan stres. " +
                            "Pertimbangkan deload week jika penurunan berlanjut.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ================================================================
// INPUT SUMMARY CARD
// ================================================================
@Composable
private fun InputSummaryCard(input: UserInput) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantLight)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "📝 Data Input",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InputChip("💤", "${input.sleepDuration}h")
                InputChip("💪", input.muscleSoreness.displayName)
                InputChip("🎯", input.userGoal.name)
                InputChip("📊", input.prevPerformance.displayName)
            }
        }
    }
}

@Composable
private fun InputChip(icon: String, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SurfaceLight
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 12.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ================================================================
// DISCLAIMER
// ================================================================
@Composable
private fun DisclaimerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightTrainingYellowLight
        ),
        border = BorderStroke(1.dp, LightTrainingYellow.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text("⚠️", fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Rekomendasi ini bersifat fisiologis berdasarkan data yang kamu masukkan. " +
                        "Sistem tidak dapat mengevaluasi postur atau teknik gerakan. " +
                        "Selalu dengarkan kondisi tubuhmu dan konsultasikan dengan pelatih profesional jika diperlukan.",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

// ================================================================
// ACTION BUTTONS
// ================================================================
@Composable
private fun ActionButtons(
    isSaved: Boolean,
    onSave: () -> Unit,
    onReInput: () -> Unit,
    onViewHistory: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Simpan Sesi
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSaved,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                disabledContainerColor = NormalTrainingGreen.copy(alpha = 0.7f),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = if (isSaved) "✅ Sesi Tersimpan" else "💾 Simpan Sesi",
                fontWeight = FontWeight.Bold
            )
        }

        // Baris tombol sekunder
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onReInput,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PrimaryBlue)
            ) {
                Text("🔄 Input Ulang", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = onViewHistory,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PrimaryBlue)
            ) {
                Text("📋 Riwayat", fontSize = 13.sp)
            }
        }
    }
}
