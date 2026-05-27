package com.ukdw.pplaicoach.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database utama untuk PPL AI Coach.
 * Menggunakan singleton pattern agar hanya ada satu instance database.
 * Semua data disimpan secara lokal untuk menjaga privasi pengguna.
 *
 * VERSI 2: Menambahkan tabel exercises, tracker_sessions, dan set_records
 * untuk fitur Workout Tracker (tracking set/rep/berat).
 */
@Database(
    entities = [
        WorkoutSessionEntity::class,
        ExerciseEntity::class,
        TrackerSessionEntity::class,
        SetRecordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO untuk fitur Expert System AI (sudah ada)
    abstract fun workoutSessionDao(): WorkoutSessionDao

    // DAO baru untuk fitur Workout Tracker
    abstract fun exerciseDao(): ExerciseDao
    abstract fun trackerSessionDao(): TrackerSessionDao
    abstract fun setRecordDao(): SetRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Mendapatkan instance database (singleton).
         * Thread-safe menggunakan synchronized block.
         * Pre-populate tabel exercises dengan 9 gerakan standar PPL.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ppl_ai_coach_database"
                )
                    // Migrasi destruktif aman — data sebelumnya hanya riwayat AI
                    .fallbackToDestructiveMigration()
                    // Callback untuk pre-populate tabel exercises
                    .addCallback(PrepopulateCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Callback untuk mengisi tabel exercises dengan data awal
         * saat database pertama kali dibuat.
         * 9 gerakan standar PPL: 3 Push, 3 Pull, 3 Legs
         */
        private class PrepopulateCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        database.exerciseDao().insertAll(getDefaultExercises())
                    }
                }
            }
        }

        /**
         * Daftar 9 gerakan standar PPL yang akan di-pre-populate.
         * Setiap gerakan memiliki instruksi singkat dan link YouTube search.
         */
        fun getDefaultExercises(): List<ExerciseEntity> = listOf(
            // === PUSH DAY (3 gerakan) ===
            ExerciseEntity(
                name = "Flat Benchpress",
                category = ExerciseCategory.PUSH.name,
                instructions = "1. Baring di flat bench, kaki menapak lantai\n" +
                        "2. Grip barbell lebih lebar dari bahu (1.5x lebar bahu)\n" +
                        "3. Angkat bar dari rack, kunci lengan di atas dada\n" +
                        "4. Turunkan bar perlahan ke tengah dada (tarik napas)\n" +
                        "5. Dorong bar ke atas hingga lengan lurus (buang napas)\n" +
                        "6. Jaga punggung sedikit melengkung, skapula tertarik",
                youtubeUrl = "https://www.youtube.com/results?search_query=flat+bench+press+tutorial+form"
            ),
            ExerciseEntity(
                name = "Dumbbell Shoulder Press",
                category = ExerciseCategory.PUSH.name,
                instructions = "1. Duduk tegak di bench 90° dengan sandaran\n" +
                        "2. Angkat dumbbell setinggi bahu, telapak menghadap depan\n" +
                        "3. Dorong dumbbell ke atas hingga lengan hampir lurus\n" +
                        "4. Jangan kunci siku sepenuhnya di atas\n" +
                        "5. Turunkan perlahan kembali ke posisi bahu\n" +
                        "6. Jaga core tetap kencang sepanjang gerakan",
                youtubeUrl = "https://www.youtube.com/results?search_query=dumbbell+shoulder+press+tutorial"
            ),
            ExerciseEntity(
                name = "Tricep Pushdown",
                category = ExerciseCategory.PUSH.name,
                instructions = "1. Berdiri menghadap cable machine, pasang straight bar/rope\n" +
                        "2. Grip bar dengan kedua tangan, siku menempel di sisi badan\n" +
                        "3. Dorong bar ke bawah hingga lengan lurus (squeeze trisep)\n" +
                        "4. Tahan kontraksi 1 detik di bawah\n" +
                        "5. Kembalikan perlahan ke posisi awal (siku 90°)\n" +
                        "6. Jaga tubuh tetap tegak, jangan condong ke depan",
                youtubeUrl = "https://www.youtube.com/results?search_query=tricep+pushdown+cable+tutorial"
            ),

            // === PULL DAY (3 gerakan) ===
            ExerciseEntity(
                name = "Barbell Row",
                category = ExerciseCategory.PULL.name,
                instructions = "1. Berdiri dengan kaki selebar bahu, grip barbell overhand\n" +
                        "2. Tekuk badan ke depan 45° (hip hinge), punggung lurus\n" +
                        "3. Biarkan bar menggantung di depan lutut\n" +
                        "4. Tarik bar ke arah perut bawah (squeeze skapula)\n" +
                        "5. Tahan kontraksi 1 detik di atas\n" +
                        "6. Turunkan perlahan, jaga punggung tetap netral",
                youtubeUrl = "https://www.youtube.com/results?search_query=barbell+row+tutorial+form"
            ),
            ExerciseEntity(
                name = "Lat Pulldown",
                category = ExerciseCategory.PULL.name,
                instructions = "1. Duduk di mesin lat pulldown, kunci paha di bawah pad\n" +
                        "2. Grip bar lebar (1.5x lebar bahu), telapak menghadap depan\n" +
                        "3. Tarik bar ke bawah menuju dada atas\n" +
                        "4. Fokus pada kontraksi lat (otot punggung samping)\n" +
                        "5. Tahan kontraksi 1 detik di bawah\n" +
                        "6. Kembalikan bar perlahan ke atas dengan kontrol",
                youtubeUrl = "https://www.youtube.com/results?search_query=lat+pulldown+tutorial+form"
            ),
            ExerciseEntity(
                name = "Hammer Curl",
                category = ExerciseCategory.PULL.name,
                instructions = "1. Berdiri tegak, pegang dumbbell di sisi badan\n" +
                        "2. Telapak tangan menghadap ke dalam (netral grip)\n" +
                        "3. Curl dumbbell ke atas dengan mengontraksi bisep\n" +
                        "4. Jaga siku tetap di sisi badan, jangan berayun\n" +
                        "5. Tahan kontraksi 1 detik di atas\n" +
                        "6. Turunkan perlahan, bisa alternating atau bersamaan",
                youtubeUrl = "https://www.youtube.com/results?search_query=hammer+curl+tutorial+form"
            ),

            // === LEG DAY (3 gerakan) ===
            ExerciseEntity(
                name = "Barbell Squat",
                category = ExerciseCategory.LEGS.name,
                instructions = "1. Letakkan barbell di upper back (bukan leher)\n" +
                        "2. Kaki selebar bahu, jari kaki sedikit keluar\n" +
                        "3. Turun perlahan hingga paha paralel atau lebih rendah\n" +
                        "4. Jaga dada tetap tegak, lutut sejajar jari kaki\n" +
                        "5. Dorong ke atas melalui tumit (buang napas)\n" +
                        "6. Kunci pinggul di atas, squeeze glute",
                youtubeUrl = "https://www.youtube.com/results?search_query=barbell+squat+tutorial+form"
            ),
            ExerciseEntity(
                name = "Romanian Deadlift",
                category = ExerciseCategory.LEGS.name,
                instructions = "1. Berdiri tegak, pegang barbell di depan paha\n" +
                        "2. Kaki selebar pinggul, lutut sedikit tekuk (soft lock)\n" +
                        "3. Hip hinge: dorong pinggul ke belakang\n" +
                        "4. Turunkan bar sepanjang kaki hingga terasa stretch hamstring\n" +
                        "5. Jaga punggung lurus sepanjang gerakan\n" +
                        "6. Dorong pinggul ke depan untuk kembali berdiri",
                youtubeUrl = "https://www.youtube.com/results?search_query=romanian+deadlift+tutorial+form"
            ),
            ExerciseEntity(
                name = "Leg Extension",
                category = ExerciseCategory.LEGS.name,
                instructions = "1. Duduk di mesin leg extension, punggung menempel sandaran\n" +
                        "2. Posisikan pad di atas pergelangan kaki\n" +
                        "3. Luruskan kaki ke depan (kontraksi quadricep)\n" +
                        "4. Tahan kontraksi 1-2 detik di atas\n" +
                        "5. Turunkan perlahan dengan kontrol (jangan jatuhkan)\n" +
                        "6. Gunakan beban yang bisa dikontrol 10-15 rep",
                youtubeUrl = "https://www.youtube.com/results?search_query=leg+extension+machine+tutorial"
            )
        )
    }
}
