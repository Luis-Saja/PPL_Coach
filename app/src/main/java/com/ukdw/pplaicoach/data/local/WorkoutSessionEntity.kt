package com.ukdw.pplaicoach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room Database untuk menyimpan data sesi latihan.
 * Setiap baris merepresentasikan satu sesi latihan harian beserta
 * input kondisi fisik dan hasil inferensi dari sistem pakar.
 *
 * Semua data disimpan secara lokal — tidak dikirim ke server manapun (privasi).
 */
@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** Timestamp sesi dalam milliseconds (System.currentTimeMillis()) */
    val timestamp: Long,

    /** Durasi tidur pengguna dalam jam (numerik, contoh: 5.5) */
    val sleepDuration: Double,

    /** Tingkat nyeri otot: "LOW" | "MEDIUM" | "HIGH" */
    val muscleSoreness: String,

    /** Tujuan latihan: "BULKING" | "CUTTING" */
    val userGoal: String,

    /** Performa sesi sebelumnya: "INCREASE" | "STABLE" | "DECREASE" */
    val prevPerformance: String,

    /** Penyesuaian beban dalam persen (contoh: -20, +10) */
    val loadAdjustment: Int,

    /** Penyesuaian volume dalam persen (contoh: -15, 0) */
    val volumeAdjustment: Int,

    /** Rekomendasi latihan: "REST_DAY" | "LIGHT_TRAINING" | "NORMAL_TRAINING" */
    val recommendation: String,

    /** Daftar rule yang aktif dalam format JSON array, contoh: ["R1","R2"] */
    val activeRules: String,

    /** Teks penjelasan lengkap dari hasil inferensi */
    val explanationText: String
)
