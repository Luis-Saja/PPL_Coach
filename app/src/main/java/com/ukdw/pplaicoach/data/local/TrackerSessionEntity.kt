package com.ukdw.pplaicoach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * === TRACKER SESSION ENTITY ===
 * Menyimpan sesi latihan tracker (terpisah dari sesi AI Expert System).
 * Setiap kali pengguna mulai workout, satu record dibuat di sini.
 * Field 'category' digunakan untuk auto-suggest hari PPL berikutnya.
 */
@Entity(tableName = "tracker_sessions")
data class TrackerSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Timestamp sesi dalam milliseconds */
    val date: Long = System.currentTimeMillis(),

    /** Kategori hari latihan: "PUSH", "PULL", atau "LEGS" */
    val category: String
)
