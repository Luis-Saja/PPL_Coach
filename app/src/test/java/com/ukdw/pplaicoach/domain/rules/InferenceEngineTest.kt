package com.ukdw.pplaicoach.domain.rules

import com.ukdw.pplaicoach.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * === UNIT TEST INFERENCE ENGINE (ENHANCED) ===
 * Memvalidasi kelima skenario uji (T1–T5) yang sudah ditentukan dalam proposal.
 * Juga menguji fitur-fitur baru: confidence score, inference trace, dan rule evaluation.
 *
 * Referensi: Tabel Skenario Uji dalam proposal TI0263 – Kecerdasan Buatan, UKDW
 */
class InferenceEngineTest {

    private lateinit var engine: InferenceEngine

    @Before
    fun setup() {
        engine = InferenceEngine()
    }

    /**
     * Skenario T1: Tidur 5 jam, Soreness High, Goal Bulking
     * Expected: Rule R1 aktif, Load -20%, Recommendation Rest Day
     */
    @Test
    fun `T1 - Sleep 5h, High Soreness, Bulking should trigger R1 with Rest Day`() {
        val input = UserInput(
            sleepDuration = 5.0,
            muscleSoreness = SorenessLevel.HIGH,
            userGoal = UserGoal.BULKING,
            prevPerformance = PrevPerformance.STABLE
        )

        val result = engine.run(input)

        assertEquals(-20, result.loadAdjustment)
        assertEquals(0, result.volumeAdjustment)
        assertEquals(RecommendationType.REST_DAY, result.recommendation)
        assertEquals(listOf("R1"), result.activeRules)
    }

    /**
     * Skenario T2: Tidur 5 jam, Soreness Medium, Goal Cutting
     * Expected: Rule R2, R5, R6 aktif, Load -15%, Volume -15%, Recommendation Light Training
     */
    @Test
    fun `T2 - Sleep 5h, Medium Soreness, Cutting should trigger R2 R5 R6 with Light Training`() {
        val input = UserInput(
            sleepDuration = 5.0,
            muscleSoreness = SorenessLevel.MEDIUM,
            userGoal = UserGoal.CUTTING,
            prevPerformance = PrevPerformance.STABLE
        )

        val result = engine.run(input)

        assertEquals(-15, result.loadAdjustment)
        assertEquals(-15, result.volumeAdjustment)
        assertEquals(RecommendationType.LIGHT_TRAINING, result.recommendation)
        assertEquals(listOf("R2", "R5", "R6"), result.activeRules)
    }

    /**
     * Skenario T3: Tidur 8 jam, Soreness Low, Goal Bulking
     * Expected: Rule R3 dan R4 aktif, Load +10%, Recommendation Normal Training
     */
    @Test
    fun `T3 - Sleep 8h, Low Soreness, Bulking should trigger R3 R4 with +10 percent load`() {
        val input = UserInput(
            sleepDuration = 8.0,
            muscleSoreness = SorenessLevel.LOW,
            userGoal = UserGoal.BULKING,
            prevPerformance = PrevPerformance.STABLE
        )

        val result = engine.run(input)

        assertEquals(10, result.loadAdjustment)
        assertEquals(0, result.volumeAdjustment)
        assertEquals(RecommendationType.NORMAL_TRAINING, result.recommendation)
        assertEquals(listOf("R3", "R4"), result.activeRules)
    }

    /**
     * Skenario T4: Tidur 7 jam, Soreness Medium, Goal Cutting
     * Expected: Rule R5 aktif, Load 0%, Volume -15%, Recommendation Normal Training
     * CATATAN: 7 jam bukan > 7, jadi R3 tidak aktif
     */
    @Test
    fun `T4 - Sleep 7h, Medium Soreness, Cutting should trigger only R5`() {
        val input = UserInput(
            sleepDuration = 7.0,
            muscleSoreness = SorenessLevel.MEDIUM,
            userGoal = UserGoal.CUTTING,
            prevPerformance = PrevPerformance.STABLE
        )

        val result = engine.run(input)

        assertEquals(0, result.loadAdjustment)
        assertEquals(-15, result.volumeAdjustment)
        assertEquals(RecommendationType.NORMAL_TRAINING, result.recommendation)
        assertEquals(listOf("R5"), result.activeRules)
    }

    /**
     * Skenario T5: Tidur 6 jam, Soreness Low, Goal Bulking
     * Expected: Rule R4 aktif, Load +5%, Recommendation Normal Training
     */
    @Test
    fun `T5 - Sleep 6h, Low Soreness, Bulking should trigger only R4 with +5 percent load`() {
        val input = UserInput(
            sleepDuration = 6.0,
            muscleSoreness = SorenessLevel.LOW,
            userGoal = UserGoal.BULKING,
            prevPerformance = PrevPerformance.STABLE
        )

        val result = engine.run(input)

        assertEquals(5, result.loadAdjustment)
        assertEquals(0, result.volumeAdjustment)
        assertEquals(RecommendationType.NORMAL_TRAINING, result.recommendation)
        assertEquals(listOf("R4"), result.activeRules)
    }

    /**
     * Test: Inference engine harus bersifat deterministik.
     */
    @Test
    fun `Inference engine should be deterministic`() {
        val input = UserInput(
            sleepDuration = 5.0,
            muscleSoreness = SorenessLevel.MEDIUM,
            userGoal = UserGoal.CUTTING,
            prevPerformance = PrevPerformance.DECREASE
        )

        val result1 = engine.run(input)
        val result2 = engine.run(input)

        assertEquals(result1.loadAdjustment, result2.loadAdjustment)
        assertEquals(result1.volumeAdjustment, result2.volumeAdjustment)
        assertEquals(result1.recommendation, result2.recommendation)
        assertEquals(result1.activeRules, result2.activeRules)
    }

    /**
     * Test: Tidak ada rule yang aktif → default Normal Training
     */
    @Test
    fun `No rules should fire when no conditions are met`() {
        val input = UserInput(
            sleepDuration = 6.5,
            muscleSoreness = SorenessLevel.HIGH,
            userGoal = UserGoal.BULKING,
            prevPerformance = PrevPerformance.STABLE
        )

        val result = engine.run(input)

        assertEquals(0, result.loadAdjustment)
        assertEquals(0, result.volumeAdjustment)
        assertEquals(RecommendationType.NORMAL_TRAINING, result.recommendation)
        assertTrue(result.activeRules.isEmpty())
    }

    // ========================
    // TEST FITUR ENHANCED: Confidence Score
    // ========================

    /**
     * Test: Confidence score harus ada dan bernilai 0.0 - 1.0
     */
    @Test
    fun `Confidence score should be between 0 and 1`() {
        val input = UserInput(
            sleepDuration = 5.0,
            muscleSoreness = SorenessLevel.HIGH,
            userGoal = UserGoal.BULKING,
            prevPerformance = PrevPerformance.STABLE
        )

        val result = engine.run(input)

        assertTrue(result.confidenceScore.value >= 0.0)
        assertTrue(result.confidenceScore.value <= 1.0)
        assertTrue(result.confidenceScore.label.isNotEmpty())
        assertTrue(result.confidenceScore.reasoning.isNotEmpty())
    }

    /**
     * Test: Recovery rule harus menghasilkan confidence lebih tinggi (sinyal lebih jelas)
     */
    @Test
    fun `Recovery rules should produce higher confidence than no rules`() {
        // T1: Recovery rule R1 aktif
        val inputRecovery = UserInput(5.0, SorenessLevel.HIGH, UserGoal.BULKING, PrevPerformance.STABLE)
        val resultRecovery = engine.run(inputRecovery)

        // No rules: Kondisi ambigu
        val inputNone = UserInput(6.5, SorenessLevel.HIGH, UserGoal.BULKING, PrevPerformance.STABLE)
        val resultNone = engine.run(inputNone)

        assertTrue(
            "Recovery confidence (${resultRecovery.confidenceScore.value}) should be >= no-rule confidence (${resultNone.confidenceScore.value})",
            resultRecovery.confidenceScore.value >= resultNone.confidenceScore.value
        )
    }

    // ========================
    // TEST FITUR ENHANCED: Inference Trace
    // ========================

    /**
     * Test: Inference trace harus memiliki 5 langkah (5 fase Forward Chaining)
     */
    @Test
    fun `Inference trace should have 5 steps for all phases`() {
        val input = UserInput(5.0, SorenessLevel.HIGH, UserGoal.BULKING, PrevPerformance.STABLE)
        val result = engine.run(input)
        val trace = result.inferenceTrace

        assertEquals(5, trace.steps.size)
        assertEquals(InferencePhase.INITIALIZATION, trace.steps[0].phase)
        assertEquals(InferencePhase.MATCH, trace.steps[1].phase)
        assertEquals(InferencePhase.CONFLICT_RESOLUTION, trace.steps[2].phase)
        assertEquals(InferencePhase.FIRE, trace.steps[3].phase)
        assertEquals(InferencePhase.CONCLUSION, trace.steps[4].phase)
    }

    /**
     * Test: Rule evaluations harus mencatat semua 6 rules
     */
    @Test
    fun `Rule evaluations should cover all 6 rules in knowledge base`() {
        val input = UserInput(5.0, SorenessLevel.HIGH, UserGoal.BULKING, PrevPerformance.STABLE)
        val result = engine.run(input)
        val trace = result.inferenceTrace

        assertEquals(6, trace.ruleEvaluations.size)
        assertEquals(6, trace.totalRulesInKB)
        assertEquals(6, trace.totalRulesEvaluated)

        // Verifikasi semua rule ID ada
        val ruleIds = trace.ruleEvaluations.map { it.ruleId }
        assertTrue(ruleIds.containsAll(listOf("R1", "R2", "R3", "R4", "R5", "R6")))
    }

    /**
     * Test: Untuk T1, hanya R1 yang matched, sisanya tidak
     */
    @Test
    fun `T1 should have exactly 1 rule matched in evaluations`() {
        val input = UserInput(5.0, SorenessLevel.HIGH, UserGoal.BULKING, PrevPerformance.STABLE)
        val result = engine.run(input)
        val trace = result.inferenceTrace

        assertEquals(1, trace.totalRulesMatched)
        assertEquals(1, trace.totalRulesFired)

        val matched = trace.ruleEvaluations.filter { it.isMatched }
        assertEquals(1, matched.size)
        assertEquals("R1", matched[0].ruleId)
    }

    /**
     * Test: Untuk T2, R2/R5/R6 matched (3 rules)
     */
    @Test
    fun `T2 should have exactly 3 rules matched in evaluations`() {
        val input = UserInput(5.0, SorenessLevel.MEDIUM, UserGoal.CUTTING, PrevPerformance.STABLE)
        val result = engine.run(input)
        val trace = result.inferenceTrace

        assertEquals(3, trace.totalRulesMatched)
        assertEquals(3, trace.totalRulesFired)

        val matched = trace.ruleEvaluations.filter { it.isMatched }.map { it.ruleId }
        assertTrue(matched.containsAll(listOf("R2", "R5", "R6")))
    }

    /**
     * Test: Processing time harus >= 0
     */
    @Test
    fun `Processing time should be non-negative`() {
        val input = UserInput(5.0, SorenessLevel.HIGH, UserGoal.BULKING, PrevPerformance.STABLE)
        val result = engine.run(input)

        assertTrue(result.inferenceTrace.processingTimeMs >= 0)
    }
}
