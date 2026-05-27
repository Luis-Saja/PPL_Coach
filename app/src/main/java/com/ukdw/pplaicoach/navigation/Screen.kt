package com.ukdw.pplaicoach.navigation

/**
 * === DEFINISI RUTE NAVIGASI ===
 * Semua rute navigasi dalam aplikasi PPL AI Coach.
 */
sealed class Screen(val route: String) {
    /** Halaman utama / dashboard */
    data object Home : Screen("home")

    /** Form input kondisi fisik harian */
    data object Input : Screen("input")

    /** Halaman hasil inferensi */
    data object Result : Screen("result")

    /** Riwayat sesi latihan */
    data object History : Screen("history")

    /** Profil pengguna dan pengaturan */
    data object Profile : Screen("profile")

    /** Mode debug/test untuk validasi skenario (tersembunyi) */
    data object TestMode : Screen("test_mode")

    /** Workout Tracker — tracking set/rep/berat harian */
    data object Tracker : Screen("tracker")

    /** Progress Chart — visualisasi progress per exercise */
    data object Progress : Screen("progress")
}
