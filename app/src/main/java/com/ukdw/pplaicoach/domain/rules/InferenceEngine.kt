package com.ukdw.pplaicoach.domain.rules

import com.ukdw.pplaicoach.domain.model.*
import kotlin.math.min

/**
 * === INFERENCE ENGINE — FORWARD CHAINING (ENHANCED) ===
 *
 * Implementasi mesin inferensi berbasis Forward Chaining untuk sistem pakar PPL AI Coach.
 * Menggunakan 6 Production Rules yang dievaluasi secara sekuensial.
 *
 * ENHANCEMENT dari versi sebelumnya:
 * - Inference Trace: merekam setiap langkah proses inferensi secara detail
 * - Confidence Score: menghitung tingkat keyakinan AI terhadap rekomendasi
 * - Rule Evaluation Log: mencatat evaluasi setiap rule dalam Knowledge Base
 * - Waktu pemrosesan: mengukur durasi komputasi dalam milidetik
 *
 * Alur Inferensi (Forward Chaining):
 * 1. INITIALIZATION  — Inisialisasi Working Memory dengan fakta dari input
 * 2. MATCH PHASE     — Evaluasi semua rule terhadap Working Memory
 * 3. CONFLICT RESOLUTION — Urutkan agenda berdasarkan prioritas (Recovery > Goal-based)
 * 4. FIRE PHASE      — Eksekusi setiap rule dalam agenda secara berurutan
 * 5. CONCLUSION      — Hitung confidence score dan buat rekomendasi akhir
 *
 * Sistem ini bersifat deterministik: input yang sama SELALU menghasilkan output yang sama.
 *
 * Knowledge Base terdiri dari 6 Production Rules:
 * - R1-R2: Recovery Rules (Prioritas 1 — Tinggi)
 * - R3-R6: Goal-Based Rules (Prioritas 2 — Rendah)
 *
 * Referensi: Proposal Akademik TI0263 – Kecerdasan Buatan, UKDW
 * Mendukung SDG 3 (Good Health and Well-Being)
 */
class InferenceEngine {

    // Jumlah total rules dalam Knowledge Base
    companion object {
        const val TOTAL_RULES_IN_KB = 6
    }

    /**
     * Menjalankan inferensi Forward Chaining berdasarkan input pengguna.
     * Seluruh proses direkam dalam InferenceTrace untuk transparansi dan explainability.
     *
     * @param input Data kondisi fisik harian pengguna (4 variabel)
     * @return Hasil inferensi berisi rekomendasi, adjustment, confidence, dan trace lengkap
     */
    fun run(input: UserInput): InferenceResult {
        val startTime = System.currentTimeMillis()
        val traceSteps = mutableListOf<InferenceStep>()
        val ruleEvaluations = mutableListOf<RuleEvaluation>()

        // ================================================================
        // FASE 1: INISIALISASI WORKING MEMORY
        // Working Memory (WM) menyimpan fakta-fakta awal dari input pengguna
        // serta variabel yang akan dimodifikasi oleh rule yang aktif.
        // Dalam Expert System, WM adalah representasi pengetahuan saat ini.
        // ================================================================
        val wm = WorkingMemory(
            sleepDuration = input.sleepDuration,
            muscleSoreness = input.muscleSoreness,
            userGoal = input.userGoal,
            prevPerformance = input.prevPerformance,
            loadAdjustment = 0,
            volumeAdjustment = 0,
            recommendation = RecommendationType.NORMAL_TRAINING
        )

        traceSteps.add(
            InferenceStep(
                phase = InferencePhase.INITIALIZATION,
                title = "Inisialisasi Working Memory",
                description = "Memuat 4 variabel input ke dalam Working Memory untuk diproses oleh Knowledge Base.",
                details = listOf(
                    "💤 Durasi Tidur: ${input.sleepDuration} jam" + categorizeSleep(input.sleepDuration),
                    "💪 Nyeri Otot: ${input.muscleSoreness.displayName} — ${input.muscleSoreness.description}",
                    "🎯 Tujuan: ${input.userGoal.displayName}",
                    "📊 Performa Sebelumnya: ${input.prevPerformance.displayName}",
                    "📦 Load Adjustment awal: 0%",
                    "📊 Volume Adjustment awal: 0%",
                    "🏷️ Rekomendasi awal: Normal Training"
                )
            )
        )

        // ================================================================
        // FASE 2: MATCH PHASE
        // Evaluasi semua 6 production rules secara sekuensial.
        // Setiap rule yang kondisinya terpenuhi akan ditambahkan ke agenda.
        // Fase ini memeriksa anteseden (IF-part) dari setiap rule.
        // ================================================================
        val agenda = mutableListOf<AgendaItem>()
        val matchDetails = mutableListOf<String>()

        // --- RECOVERY RULES (Prioritas 1 — Tinggi) ---
        // Rule ini mengutamakan pemulihan tubuh pengguna.
        // Recovery rules memiliki prioritas lebih tinggi karena kesehatan > performa.

        // R1: Jika tidur kurang dari 6 jam DAN nyeri otot tinggi → Rest Day
        // Alasan: Kombinasi kurang tidur dan DOMS parah sangat berisiko cedera
        val r1Matched = wm.sleepDuration < 6 && wm.muscleSoreness == SorenessLevel.HIGH
        ruleEvaluations.add(
            RuleEvaluation(
                ruleId = "R1",
                ruleName = "Recovery Kritis",
                condition = "SleepDuration < 6 AND MuscleSoreness = High",
                isMatched = r1Matched,
                priority = 1,
                priorityLabel = "Recovery (Prioritas Tinggi)"
            )
        )
        if (r1Matched) {
            agenda.add(AgendaItem("R1", priority = 1))
            matchDetails.add("✅ R1 COCOK — Tidur < 6 jam (${wm.sleepDuration}h) DAN Soreness High")
        } else {
            matchDetails.add("❌ R1 tidak cocok — " + explainMismatch("R1", wm))
        }

        // R2: Jika tidur kurang dari 6 jam DAN nyeri otot sedang → Kurangi beban & volume
        // Alasan: Kurang tidur + pegal menandakan pemulihan belum optimal
        val r2Matched = wm.sleepDuration < 6 && wm.muscleSoreness == SorenessLevel.MEDIUM
        ruleEvaluations.add(
            RuleEvaluation(
                ruleId = "R2",
                ruleName = "Recovery Moderat",
                condition = "SleepDuration < 6 AND MuscleSoreness = Medium",
                isMatched = r2Matched,
                priority = 1,
                priorityLabel = "Recovery (Prioritas Tinggi)"
            )
        )
        if (r2Matched) {
            agenda.add(AgendaItem("R2", priority = 1))
            matchDetails.add("✅ R2 COCOK — Tidur < 6 jam (${wm.sleepDuration}h) DAN Soreness Medium")
        } else {
            matchDetails.add("❌ R2 tidak cocok — " + explainMismatch("R2", wm))
        }

        // R3: Jika tidur lebih dari 7 jam DAN tidak ada nyeri otot → Tambah beban
        // Alasan: Tidur cukup + otot segar = kondisi optimal untuk progressive overload
        val r3Matched = wm.sleepDuration > 7 && wm.muscleSoreness == SorenessLevel.LOW
        ruleEvaluations.add(
            RuleEvaluation(
                ruleId = "R3",
                ruleName = "Kondisi Optimal",
                condition = "SleepDuration > 7 AND MuscleSoreness = Low",
                isMatched = r3Matched,
                priority = 2,
                priorityLabel = "Goal-Based (Prioritas Rendah)"
            )
        )
        if (r3Matched) {
            agenda.add(AgendaItem("R3", priority = 2))
            matchDetails.add("✅ R3 COCOK — Tidur > 7 jam (${wm.sleepDuration}h) DAN Soreness Low")
        } else {
            matchDetails.add("❌ R3 tidak cocok — " + explainMismatch("R3", wm))
        }

        // --- GOAL-BASED RULES (Prioritas 2 — Rendah) ---
        // Rule ini menyesuaikan latihan berdasarkan tujuan pengguna.

        // R4: Jika tujuan Bulking DAN tidak ada nyeri otot → Progressive Overload
        // Alasan: Otot segar + tujuan bulking = saatnya naikkan beban
        val r4Matched = wm.userGoal == UserGoal.BULKING && wm.muscleSoreness == SorenessLevel.LOW
        ruleEvaluations.add(
            RuleEvaluation(
                ruleId = "R4",
                ruleName = "Progressive Overload",
                condition = "UserGoal = Bulking AND MuscleSoreness = Low",
                isMatched = r4Matched,
                priority = 2,
                priorityLabel = "Goal-Based (Prioritas Rendah)"
            )
        )
        if (r4Matched) {
            agenda.add(AgendaItem("R4", priority = 2))
            matchDetails.add("✅ R4 COCOK — Goal Bulking DAN Soreness Low")
        } else {
            matchDetails.add("❌ R4 tidak cocok — " + explainMismatch("R4", wm))
        }

        // R5: Jika tujuan Cutting DAN nyeri otot sedang → Kurangi volume
        // Alasan: Saat cutting, tubuh dalam defisit kalori sehingga recovery lebih lambat
        val r5Matched = wm.userGoal == UserGoal.CUTTING && wm.muscleSoreness == SorenessLevel.MEDIUM
        ruleEvaluations.add(
            RuleEvaluation(
                ruleId = "R5",
                ruleName = "Cutting Volume Control",
                condition = "UserGoal = Cutting AND MuscleSoreness = Medium",
                isMatched = r5Matched,
                priority = 2,
                priorityLabel = "Goal-Based (Prioritas Rendah)"
            )
        )
        if (r5Matched) {
            agenda.add(AgendaItem("R5", priority = 2))
            matchDetails.add("✅ R5 COCOK — Goal Cutting DAN Soreness Medium")
        } else {
            matchDetails.add("❌ R5 tidak cocok — " + explainMismatch("R5", wm))
        }

        // R6: Jika tujuan Cutting DAN tidur kurang dari 6 jam → Light Training
        // Alasan: Defisit kalori + kurang tidur = sangat rentan terhadap overtraining
        val r6Matched = wm.userGoal == UserGoal.CUTTING && wm.sleepDuration < 6
        ruleEvaluations.add(
            RuleEvaluation(
                ruleId = "R6",
                ruleName = "Cutting Safety",
                condition = "UserGoal = Cutting AND SleepDuration < 6",
                isMatched = r6Matched,
                priority = 2,
                priorityLabel = "Goal-Based (Prioritas Rendah)"
            )
        )
        if (r6Matched) {
            agenda.add(AgendaItem("R6", priority = 2))
            matchDetails.add("✅ R6 COCOK — Goal Cutting DAN Tidur < 6 jam (${wm.sleepDuration}h)")
        } else {
            matchDetails.add("❌ R6 tidak cocok — " + explainMismatch("R6", wm))
        }

        val matchedCount = agenda.size
        traceSteps.add(
            InferenceStep(
                phase = InferencePhase.MATCH,
                title = "Evaluasi ${TOTAL_RULES_IN_KB} Production Rules",
                description = "Mengevaluasi setiap rule dalam Knowledge Base terhadap fakta di Working Memory. " +
                        "Ditemukan $matchedCount dari $TOTAL_RULES_IN_KB rule yang cocok.",
                details = matchDetails
            )
        )

        // ================================================================
        // FASE 3: CONFLICT RESOLUTION
        // Urutkan agenda berdasarkan prioritas (angka kecil = prioritas tinggi).
        // Recovery rules (prioritas 1) dieksekusi sebelum Goal-based rules (prioritas 2).
        // Strategi: Priority-based ordering (Most Specific First).
        // ================================================================
        val beforeSort = agenda.map { "${it.ruleId} (P${it.priority})" }
        agenda.sortBy { it.priority }
        val afterSort = agenda.map { "${it.ruleId} (P${it.priority})" }

        val conflictDetails = mutableListOf<String>()
        if (agenda.size > 1) {
            conflictDetails.add("Agenda sebelum resolusi: ${beforeSort.joinToString(", ")}")
            conflictDetails.add("Agenda setelah resolusi: ${afterSort.joinToString(", ")}")
            conflictDetails.add("Strategi: Priority-based ordering — Recovery rules (P1) dieksekusi sebelum Goal-based (P2)")

            val p1Count = agenda.count { it.priority == 1 }
            val p2Count = agenda.count { it.priority == 2 }
            if (p1Count > 0 && p2Count > 0) {
                conflictDetails.add("⚠️ Konflik terdeteksi: $p1Count Recovery rule + $p2Count Goal-based rule")
                conflictDetails.add("→ Recovery rules akan dieksekusi terlebih dahulu (konservatif)")
            }
        } else if (agenda.size == 1) {
            conflictDetails.add("Hanya 1 rule yang cocok — tidak ada konflik")
            conflictDetails.add("Rule ${agenda[0].ruleId} langsung masuk ke Fire Phase")
        } else {
            conflictDetails.add("Tidak ada rule yang cocok — agenda kosong")
            conflictDetails.add("Rekomendasi default: Normal Training tanpa adjustment")
        }

        traceSteps.add(
            InferenceStep(
                phase = InferencePhase.CONFLICT_RESOLUTION,
                title = "Resolusi Konflik — ${agenda.size} Rule dalam Agenda",
                description = if (agenda.size > 1)
                    "Mengurutkan ${agenda.size} rule berdasarkan prioritas untuk menentukan urutan eksekusi."
                else if (agenda.size == 1)
                    "Satu rule cocok — langsung dieksekusi tanpa konflik."
                else
                    "Tidak ada rule yang cocok — menggunakan rekomendasi default.",
                details = conflictDetails
            )
        )

        // ================================================================
        // FASE 4: FIRE PHASE
        // Eksekusi setiap rule dalam agenda secara berurutan.
        // Untuk load adjustment: jika dua rule reduksi aktif, ambil nilai
        // TERBESAR (paling konservatif) menggunakan fungsi MIN.
        // ================================================================
        val fireDetails = mutableListOf<String>()

        for (item in agenda) {
            when (item.ruleId) {
                "R1" -> {
                    // R1: Tidur < 6 jam + Soreness High → Beban -20%, Rest Day
                    wm.loadAdjustment = -20
                    wm.recommendation = RecommendationType.REST_DAY
                    wm.activeRules.add("R1")
                    wm.explanations.add(
                        "R1 — Durasi tidur < 6 jam dengan soreness High\n→ Beban dikurangi 20%, disarankan Rest Day"
                    )
                    fireDetails.add("🔥 R1 FIRED: load = -20%, rekomendasi = Rest Day")
                }

                "R2" -> {
                    // R2: Tidur < 6 jam + Soreness Medium → Beban -10%, Volume -10%
                    val prevLoad = wm.loadAdjustment
                    wm.loadAdjustment = min(wm.loadAdjustment, -10)
                    wm.volumeAdjustment = -10
                    wm.activeRules.add("R2")
                    wm.explanations.add(
                        "R2 — Durasi tidur < 6 jam dengan soreness Medium\n→ Beban dikurangi 10%, volume dikurangi 10%"
                    )
                    fireDetails.add("🔥 R2 FIRED: load = MIN($prevLoad, -10) = ${wm.loadAdjustment}%, volume = -10%")
                }

                "R3" -> {
                    // R3: Tidur > 7 jam + Soreness Low → Beban +5%
                    val prevLoad = wm.loadAdjustment
                    wm.loadAdjustment = wm.loadAdjustment + 5
                    wm.activeRules.add("R3")
                    wm.explanations.add(
                        "R3 — Durasi tidur > 7 jam dengan soreness Low\n→ Beban ditambah 5%"
                    )
                    fireDetails.add("🔥 R3 FIRED: load = $prevLoad + 5 = ${wm.loadAdjustment}%")
                }

                "R4" -> {
                    // R4: Bulking + Soreness Low → Progressive Overload (+5%)
                    val prevLoad = wm.loadAdjustment
                    wm.loadAdjustment = wm.loadAdjustment + 5
                    wm.activeRules.add("R4")
                    wm.explanations.add(
                        "R4 — Tujuan Bulking dengan soreness Low\n→ Progressive Overload, beban ditambah 5%"
                    )
                    fireDetails.add("🔥 R4 FIRED: load = $prevLoad + 5 = ${wm.loadAdjustment}% (Progressive Overload)")
                }

                "R5" -> {
                    // R5: Cutting + Soreness Medium → Volume -15%
                    val prevVol = wm.volumeAdjustment
                    wm.volumeAdjustment = min(wm.volumeAdjustment, -15)
                    wm.activeRules.add("R5")
                    wm.explanations.add(
                        "R5 — Tujuan Cutting dengan soreness Medium\n→ Volume dikurangi 15%"
                    )
                    fireDetails.add("🔥 R5 FIRED: volume = MIN($prevVol, -15) = ${wm.volumeAdjustment}%")
                }

                "R6" -> {
                    // R6: Cutting + Tidur < 6 jam → Beban -15%, Light Training
                    val prevLoad = wm.loadAdjustment
                    wm.loadAdjustment = min(wm.loadAdjustment, -15)
                    wm.recommendation = RecommendationType.LIGHT_TRAINING
                    wm.activeRules.add("R6")
                    wm.explanations.add(
                        "R6 — Tujuan Cutting dengan tidur < 6 jam\n→ Beban dikurangi 15%, sesi Light Training"
                    )
                    fireDetails.add("🔥 R6 FIRED: load = MIN($prevLoad, -15) = ${wm.loadAdjustment}%, rekomendasi = Light Training")
                }
            }
        }

        if (fireDetails.isEmpty()) {
            fireDetails.add("Tidak ada rule yang dieksekusi — menggunakan default")
        }
        fireDetails.add("─── Hasil Working Memory setelah Fire Phase ───")
        fireDetails.add("📦 Load Adjustment: ${wm.loadAdjustment}%")
        fireDetails.add("📊 Volume Adjustment: ${wm.volumeAdjustment}%")
        fireDetails.add("🏷️ Rekomendasi: ${wm.recommendation.displayName}")

        traceSteps.add(
            InferenceStep(
                phase = InferencePhase.FIRE,
                title = "Eksekusi ${agenda.size} Rule",
                description = if (agenda.isNotEmpty())
                    "Menjalankan setiap rule yang cocok secara berurutan. " +
                            "Nilai reduksi menggunakan fungsi MIN untuk hasil paling konservatif (aman)."
                else
                    "Tidak ada rule yang dieksekusi. Working Memory tetap pada nilai default.",
                details = fireDetails
            )
        )

        // ================================================================
        // FASE 5: CONCLUSION — Hitung Confidence Score & Buat Rekomendasi
        // Confidence Score dihitung berdasarkan:
        // - Jumlah rules yang cocok vs total rules
        // - Konsistensi antara recovery rules dan goal-based rules
        // - Tingkat spesifisitas rekomendasi
        // ================================================================
        val contextualMessage = generateContextualMessage(wm)
        val confidenceScore = calculateConfidence(wm, ruleEvaluations, agenda)

        val conclusionDetails = mutableListOf(
            "🎯 Rekomendasi Akhir: ${wm.recommendation.displayName}",
            "📦 Load Adjustment: ${formatAdjustment(wm.loadAdjustment)}",
            "📊 Volume Adjustment: ${formatAdjustment(wm.volumeAdjustment)}",
            "🧠 Confidence Score: ${String.format("%.0f", confidenceScore.value * 100)}% (${confidenceScore.label})",
            "💡 ${confidenceScore.reasoning}"
        )

        traceSteps.add(
            InferenceStep(
                phase = InferencePhase.CONCLUSION,
                title = "Rekomendasi: ${wm.recommendation.displayName} (${String.format("%.0f", confidenceScore.value * 100)}% confidence)",
                description = contextualMessage,
                details = conclusionDetails
            )
        )

        val endTime = System.currentTimeMillis()

        // ================================================================
        // BANGUN INFERENCE TRACE
        // ================================================================
        val trace = InferenceTrace(
            steps = traceSteps,
            ruleEvaluations = ruleEvaluations,
            totalRulesInKB = TOTAL_RULES_IN_KB,
            totalRulesEvaluated = TOTAL_RULES_IN_KB,
            totalRulesMatched = matchedCount,
            totalRulesFired = wm.activeRules.size,
            processingTimeMs = endTime - startTime
        )

        // ================================================================
        // RETURN HASIL INFERENSI LENGKAP
        // ================================================================
        return InferenceResult(
            loadAdjustment = wm.loadAdjustment,
            volumeAdjustment = wm.volumeAdjustment,
            recommendation = wm.recommendation,
            activeRules = wm.activeRules.toList(),
            explanations = wm.explanations.toList(),
            contextualMessage = contextualMessage,
            confidenceScore = confidenceScore,
            inferenceTrace = trace
        )
    }

    // ================================================================
    // PERHITUNGAN CONFIDENCE SCORE
    // Confidence Score mengukur tingkat keyakinan AI terhadap rekomendasi.
    // Semakin banyak data yang mendukung, semakin tinggi confidence.
    // ================================================================
    private fun calculateConfidence(
        wm: WorkingMemory,
        evaluations: List<RuleEvaluation>,
        agenda: List<AgendaItem>
    ): ConfidenceScore {
        var score = 0.5 // Skor dasar

        // Faktor 1: Jumlah rules yang cocok (lebih banyak = lebih yakin)
        val matchedRatio = agenda.size.toDouble() / TOTAL_RULES_IN_KB
        score += matchedRatio * 0.2

        // Faktor 2: Apakah ada recovery rule yang aktif? (lebih yakin karena jelas)
        val hasRecoveryRule = agenda.any { it.priority == 1 }
        if (hasRecoveryRule) {
            score += 0.15 // Recovery rules sangat jelas dan tegas
        }

        // Faktor 3: Konsistensi arah adjustment (semua negatif ATAU semua positif)
        val loadDir = if (wm.loadAdjustment > 0) 1 else if (wm.loadAdjustment < 0) -1 else 0
        val volDir = if (wm.volumeAdjustment > 0) 1 else if (wm.volumeAdjustment < 0) -1 else 0
        if (loadDir != 0 && volDir != 0 && loadDir == volDir) {
            score += 0.1 // Arah konsisten
        } else if (wm.volumeAdjustment == 0 && wm.loadAdjustment != 0) {
            score += 0.05 // Hanya load yang berubah, masih konsisten
        }

        // Faktor 4: Tidak ada rule yang cocok (kurang yakin karena kondisi ambigu)
        if (agenda.isEmpty()) {
            score = 0.6 // Default — kondisi tidak memenuhi rule apapun
        }

        // Faktor 5: Performance decrease sebagai sinyal tambahan
        if (wm.prevPerformance == PrevPerformance.DECREASE) {
            score += 0.05 // Ada data tambahan yang memperkuat rekomendasi konservatif
        }

        // Clamp ke 0.0 - 1.0
        score = score.coerceIn(0.0, 1.0)

        // Tentukan label
        val label = when {
            score >= 0.85 -> "Sangat Tinggi"
            score >= 0.70 -> "Tinggi"
            score >= 0.55 -> "Sedang"
            else -> "Rendah"
        }

        // Buat reasoning
        val reasoning = when {
            hasRecoveryRule && agenda.size >= 2 ->
                "Beberapa indikator recovery jelas terdeteksi, memberikan basis kuat untuk rekomendasi ini."
            hasRecoveryRule ->
                "Rule recovery aktif memberikan sinyal kuat untuk rekomendasi pemulihan."
            agenda.size >= 2 ->
                "Beberapa rule goal-based saling mendukung, meningkatkan keyakinan rekomendasi."
            agenda.size == 1 ->
                "Satu rule cocok dengan kondisi, rekomendasi cukup spesifik."
            else ->
                "Kondisi tidak memenuhi rule spesifik — rekomendasi berdasarkan default yang aman."
        }

        return ConfidenceScore(
            value = score,
            label = label,
            reasoning = reasoning
        )
    }

    // ================================================================
    // FUNGSI HELPER: Kategorisasi Durasi Tidur
    // ================================================================
    private fun categorizeSleep(hours: Double): String {
        return when {
            hours < 6 -> " ⚠️ (Kurang)"
            hours <= 7 -> " 😐 (Cukup)"
            else -> " ✅ (Optimal)"
        }
    }

    // ================================================================
    // FUNGSI HELPER: Penjelasan Mengapa Rule Tidak Cocok
    // ================================================================
    private fun explainMismatch(ruleId: String, wm: WorkingMemory): String {
        return when (ruleId) {
            "R1" -> {
                val reasons = mutableListOf<String>()
                if (wm.sleepDuration >= 6) reasons.add("tidur ${wm.sleepDuration}h (≥ 6)")
                if (wm.muscleSoreness != SorenessLevel.HIGH) reasons.add("soreness ${wm.muscleSoreness.displayName} (≠ High)")
                reasons.joinToString(", ")
            }
            "R2" -> {
                val reasons = mutableListOf<String>()
                if (wm.sleepDuration >= 6) reasons.add("tidur ${wm.sleepDuration}h (≥ 6)")
                if (wm.muscleSoreness != SorenessLevel.MEDIUM) reasons.add("soreness ${wm.muscleSoreness.displayName} (≠ Medium)")
                reasons.joinToString(", ")
            }
            "R3" -> {
                val reasons = mutableListOf<String>()
                if (wm.sleepDuration <= 7) reasons.add("tidur ${wm.sleepDuration}h (≤ 7)")
                if (wm.muscleSoreness != SorenessLevel.LOW) reasons.add("soreness ${wm.muscleSoreness.displayName} (≠ Low)")
                reasons.joinToString(", ")
            }
            "R4" -> {
                val reasons = mutableListOf<String>()
                if (wm.userGoal != UserGoal.BULKING) reasons.add("goal ${wm.userGoal.displayName} (≠ Bulking)")
                if (wm.muscleSoreness != SorenessLevel.LOW) reasons.add("soreness ${wm.muscleSoreness.displayName} (≠ Low)")
                reasons.joinToString(", ")
            }
            "R5" -> {
                val reasons = mutableListOf<String>()
                if (wm.userGoal != UserGoal.CUTTING) reasons.add("goal ${wm.userGoal.displayName} (≠ Cutting)")
                if (wm.muscleSoreness != SorenessLevel.MEDIUM) reasons.add("soreness ${wm.muscleSoreness.displayName} (≠ Medium)")
                reasons.joinToString(", ")
            }
            "R6" -> {
                val reasons = mutableListOf<String>()
                if (wm.userGoal != UserGoal.CUTTING) reasons.add("goal ${wm.userGoal.displayName} (≠ Cutting)")
                if (wm.sleepDuration >= 6) reasons.add("tidur ${wm.sleepDuration}h (≥ 6)")
                reasons.joinToString(", ")
            }
            else -> "Kondisi tidak terpenuhi"
        }
    }

    // ================================================================
    // FUNGSI HELPER: Format Adjustment
    // ================================================================
    private fun formatAdjustment(value: Int): String {
        return when {
            value > 0 -> "+$value%"
            value < 0 -> "$value%"
            else -> "0% (tidak ada perubahan)"
        }
    }

    // ================================================================
    // GENERATE PESAN KONTEKSTUAL
    // ================================================================
    private fun generateContextualMessage(wm: WorkingMemory): String {
        return when (wm.recommendation) {
            RecommendationType.REST_DAY -> {
                "Kondisi tubuhmu belum optimal untuk latihan hari ini. " +
                        "Durasi tidurmu kurang dan tingkat nyeri otot tinggi. " +
                        "Disarankan untuk istirahat total (Rest Day) agar tubuh bisa pulih sepenuhnya. " +
                        "Gunakan waktu ini untuk stretching ringan atau foam rolling."
            }

            RecommendationType.LIGHT_TRAINING -> {
                val loadText = if (wm.loadAdjustment != 0) "beban ${wm.loadAdjustment}%" else ""
                val volText =
                    if (wm.volumeAdjustment != 0) "volume ${wm.volumeAdjustment}%" else ""
                val adjustments =
                    listOf(loadText, volText).filter { it.isNotEmpty() }.joinToString(" dan ")

                "Kondisi tubuhmu belum optimal. Disarankan Light Training hari ini" +
                        if (adjustments.isNotEmpty()) " dengan $adjustments dari sesi sebelumnya." else "."
            }

            RecommendationType.NORMAL_TRAINING -> {
                if (wm.loadAdjustment > 0) {
                    "Tubuhmu dalam kondisi prima! Saatnya push harder dengan tambahan beban +${wm.loadAdjustment}% dari sesi sebelumnya. " +
                            "Pastikan teknik tetap terjaga saat menaikkan beban."
                } else if (wm.volumeAdjustment < 0) {
                    "Kamu bisa latihan normal hari ini, namun volume dikurangi ${wm.volumeAdjustment}% " +
                            "untuk menyesuaikan dengan kondisi tubuhmu saat ini."
                } else if (wm.activeRules.isEmpty()) {
                    "Kondisi tubuhmu optimal! Lanjutkan latihan normal sesuai program PPL-mu. " +
                            "Tidak ada penyesuaian khusus yang diperlukan hari ini."
                } else {
                    "Kamu bisa melanjutkan latihan normal hari ini. " +
                            "Pastikan tetap mendengarkan tubuhmu selama sesi latihan."
                }
            }
        }
    }
}
