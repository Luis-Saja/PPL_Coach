package com.ukdw.pplaicoach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * === EXERCISE ENTITY ===
 * Menyimpan daftar gerakan latihan PPL (Push/Pull/Legs).
 * Tabel ini di-pre-populate saat database pertama kali dibuat
 * dengan 9 gerakan standar PPL.
 */

/** Kategori gerakan berdasarkan split PPL */
enum class ExerciseCategory(val displayName: String) {
    PUSH("Push Day 💪"),
    PULL("Pull Day 🏋️"),
    LEGS("Leg Day 🦵")
}

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /** Nama gerakan (contoh: "Flat Benchpress") */
    val name: String,

    /** Kategori PPL: PUSH, PULL, atau LEGS */
    val category: String,

    /** Instruksi langkah-demi-langkah cara melakukan gerakan */
    val instructions: String,

    /** URL YouTube untuk video tutorial gerakan */
    val youtubeUrl: String
)
