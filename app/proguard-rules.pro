# PPL AI Coach ProGuard Rules
# Tidak ada custom rules yang diperlukan saat ini
# Tambahkan rules di sini jika menggunakan library yang membutuhkan konfigurasi khusus

-keepattributes Signature
-keepattributes *Annotation*

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.ukdw.pplaicoach.domain.model.** { *; }
-keep class com.ukdw.pplaicoach.data.local.** { *; }
