package com.ukdw.pplaicoach.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * === SET RECORD ENTITY ===
 * Menyimpan data setiap set yang dicatat pengguna selama sesi latihan.
 * Setiap set mencatat: berat, repetisi, dan tipe intensitas.
 * Foreign key ke exercise dan tracker_session.
 */

/** Tipe intensitas untuk teknik latihan tingkat lanjut */
enum class IntensityType(val displayName: String) {
    NORMAL("Normal"),
    DROPSET("Dropset"),
    TO_FAILURE("To Failure")
}

@Entity(
    tableName = "set_records",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TrackerSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exerciseId"]),
        Index(value = ["sessionId"])
    ]
)
data class SetRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** ID gerakan yang dilakukan */
    val exerciseId: Int,

    /** ID sesi tracker tempat set ini dicatat */
    val sessionId: Long,

    /** Berat beban dalam kilogram */
    val weightInKg: Double,

    /** Jumlah repetisi */
    val reps: Int,

    /** Tipe intensitas: NORMAL, DROPSET, atau TO_FAILURE */
    val intensityType: String = IntensityType.NORMAL.name
)
