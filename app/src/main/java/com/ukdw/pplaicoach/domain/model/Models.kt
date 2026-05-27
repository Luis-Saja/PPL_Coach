package com.ukdw.pplaicoach.domain.model

/**
 * === MODEL DOMAIN PPL AI COACH ===
 * Berisi semua enum dan data class yang digunakan dalam sistem inferensi
 * berbasis Forward Chaining (Rule-Based Expert System).
 *
 * Sesuai dengan spesifikasi proposal akademik TI0263 – Kecerdasan Buatan, UKDW.
 * Mendukung SDG 3 (Good Health and Well-Being).
 */

// ========================
// ENUM: Tingkat Nyeri Otot (DOMS)
// ========================
enum class SorenessLevel(val displayName: String, val description: String) {
    LOW("Low", "Tidak ada rasa sakit, otot terasa segar"),
    MEDIUM("Medium", "Sedikit pegal, masih nyaman bergerak"),
    HIGH("High", "Sangat pegal, sulit mengangkat lengan/kaki")
}

// ========================
// ENUM: Tujuan Latihan Pengguna
// ========================
enum class UserGoal(val displayName: String, val description: String) {
    BULKING("Bulking 💪", "Menambah massa otot dengan surplus kalori dan beban berat"),
    CUTTING("Cutting 🔥", "Memangkas lemak tubuh dengan defisit kalori dan volume tinggi")
}

// ========================
// ENUM: Performa Sesi Sebelumnya
// ========================
enum class PrevPerformance(val displayName: String) {
    INCREASE("Meningkat ↑"),
    STABLE("Stabil →"),
    DECREASE("Menurun ↓")
}

// ========================
// ENUM: Tipe Rekomendasi Latihan
// ========================
enum class RecommendationType(val displayName: String) {
    REST_DAY("Rest Day"),
    LIGHT_TRAINING("Light Training"),
    NORMAL_TRAINING("Normal Training")
}

// ========================
// ENUM: Fase dalam proses Inferensi (Forward Chaining)
// ========================
enum class InferencePhase(val displayName: String, val icon: String) {
    INITIALIZATION("Inisialisasi Working Memory", "🧠"),
    MATCH("Match Phase — Evaluasi Rules", "🔍"),
    CONFLICT_RESOLUTION("Conflict Resolution — Prioritas", "⚖️"),
    FIRE("Fire Phase — Eksekusi Rules", "🔥"),
    CONCLUSION("Kesimpulan & Rekomendasi", "✅")
}

// ========================
// Data Class: Input Pengguna (4 Variabel Sistem Pakar)
// ========================
data class UserInput(
    val sleepDuration: Double,          // Durasi tidur dalam jam (numerik)
    val muscleSoreness: SorenessLevel,  // Tingkat nyeri otot
    val userGoal: UserGoal,             // Tujuan latihan: Bulking atau Cutting
    val prevPerformance: PrevPerformance // Performa sesi sebelumnya
)

// ========================
// Data Class: Skor Kepercayaan (Confidence Score)
// Mengukur tingkat keyakinan AI terhadap rekomendasi yang diberikan.
// ========================
data class ConfidenceScore(
    val value: Double,      // Nilai 0.0 - 1.0
    val label: String,      // Label: "Rendah", "Sedang", "Tinggi", "Sangat Tinggi"
    val reasoning: String   // Alasan mengapa skor ini diberikan
)

// ========================
// Data Class: Evaluasi Satu Rule
// Merekam hasil evaluasi setiap production rule dalam Knowledge Base.
// ========================
data class RuleEvaluation(
    val ruleId: String,         // ID rule: "R1" sampai "R6"
    val ruleName: String,       // Nama deskriptif rule
    val condition: String,      // Kondisi rule dalam bentuk readable
    val isMatched: Boolean,     // Apakah kondisi terpenuhi?
    val priority: Int,          // Prioritas: 1 = Recovery (tinggi), 2 = Goal-based (rendah)
    val priorityLabel: String   // "Recovery (Prioritas Tinggi)" atau "Goal-Based (Prioritas Rendah)"
)

// ========================
// Data Class: Langkah Inferensi (Inference Step)
// Merekam setiap langkah dalam proses Forward Chaining.
// ========================
data class InferenceStep(
    val phase: InferencePhase,  // Fase inferensi saat ini
    val title: String,          // Judul langkah
    val description: String,    // Deskripsi detail langkah
    val details: List<String> = emptyList() // Detail tambahan (sub-steps)
)

// ========================
// Data Class: Jejak Inferensi Lengkap (Inference Trace)
// Merekam seluruh proses Forward Chaining dari awal hingga akhir.
// ========================
data class InferenceTrace(
    val steps: List<InferenceStep>,             // Langkah-langkah proses inferensi
    val ruleEvaluations: List<RuleEvaluation>,  // Evaluasi setiap rule
    val totalRulesInKB: Int,                    // Total rules dalam Knowledge Base
    val totalRulesEvaluated: Int,               // Total rules yang dievaluasi
    val totalRulesMatched: Int,                 // Total rules yang cocok (matched)
    val totalRulesFired: Int,                   // Total rules yang dieksekusi (fired)
    val processingTimeMs: Long                  // Waktu pemrosesan (milidetik)
)

// ========================
// Data Class: Hasil Inferensi dari Inference Engine (ENHANCED)
// ========================
data class InferenceResult(
    val loadAdjustment: Int,                    // Persentase penyesuaian beban (contoh: -20, +10)
    val volumeAdjustment: Int,                  // Persentase penyesuaian volume (contoh: -15, 0)
    val recommendation: RecommendationType,     // Rekomendasi latihan utama
    val activeRules: List<String>,              // Daftar rule yang aktif (contoh: ["R1", "R3"])
    val explanations: List<String>,             // Penjelasan untuk setiap rule yang aktif
    val contextualMessage: String,              // Pesan kontekstual untuk pengguna
    // === ENHANCEMENT: Data AI Tambahan ===
    val confidenceScore: ConfidenceScore,        // Skor kepercayaan AI
    val inferenceTrace: InferenceTrace           // Jejak proses inferensi lengkap
)

// ========================
// Data Class: Working Memory (digunakan internal oleh Inference Engine)
// ========================
data class WorkingMemory(
    val sleepDuration: Double,
    val muscleSoreness: SorenessLevel,
    val userGoal: UserGoal,
    val prevPerformance: PrevPerformance,
    var loadAdjustment: Int = 0,
    var volumeAdjustment: Int = 0,
    var recommendation: RecommendationType = RecommendationType.NORMAL_TRAINING,
    val activeRules: MutableList<String> = mutableListOf(),
    val explanations: MutableList<String> = mutableListOf()
)

// ========================
// Data Class: Item Agenda (untuk Conflict Resolution)
// ========================
data class AgendaItem(
    val ruleId: String,   // ID rule: "R1" sampai "R6"
    val priority: Int     // Prioritas: 1 = Recovery (tinggi), 2 = Goal-based (rendah)
)
