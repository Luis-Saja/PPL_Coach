#  PPL AI Coach

> **Aplikasi Android Workout Coach Berbasis AI (Offline)**
>
> Mata Kuliah: TI0263 – Kecerdasan Buatan, Universitas Kristen Duta Wacana (UKDW)

Aplikasi mobile berbasis **Expert System (Sistem Pakar)** dengan metode **Forward Chaining** untuk membantu pengguna merencanakan dan melacak program latihan **Push-Pull-Legs (PPL)**. Semua proses AI berjalan **100% offline** di perangkat — tanpa internet, tanpa server.

---

##  Fitur Utama

| Fitur | Deskripsi |
|-------|-----------|
|  **AI Expert System** | Forward Chaining (6 rules) untuk rekomendasi latihan berdasarkan kondisi fisik |
| **Workout Tracker** | Catat set, reps, dan berat per gerakan dengan pilihan Push/Pull/Legs day |
|  **Rest Timer** | Countdown timer melingkar (60/90/120 detik) otomatis setelah log set |
|  **Progress Chart** | Visualisasi progress max weight per exercise menggunakan Vico chart |
|  **Auto-Suggest PPL Day** | Otomatis menyarankan hari berikutnya (Push→Pull→Legs→Push) |
|  **Exercise Guide** | Instruksi langkah-demi-langkah + link video YouTube untuk setiap gerakan |
|  **100% Offline** | Semua data tersimpan lokal di Room Database |

---

##  Tech Stack

- **Bahasa:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Arsitektur:** MVVM + Clean Architecture + Repository Pattern
- **Database:** Room (SQLite)
- **Chart:** Vico (Compose-native charting)
- **Navigation:** Jetpack Navigation Compose
- **Minimum SDK:** API 24 (Android 7.0)

---

##  Cara Menjalankan Aplikasi

### Prasyarat

1. **Android Studio** (Hedgehog 2023.1.1 atau lebih baru)
2. **JDK 17** (sudah termasuk di Android Studio)
3. **Android SDK** dengan compileSdk 34
4. **Emulator** atau **perangkat Android fisik** (min. Android 7.0 / API 24)

### Langkah-langkah

#### 1. Clone Repository

```bash
git clone https://github.com/<username>/PPLAICoach.git
cd PPLAICoach
```

#### 2. Buka di Android Studio

- Buka **Android Studio**
- Pilih **File → Open** → arahkan ke folder `PPLAICoach`
- Tunggu Gradle sync selesai (Android Studio akan otomatis mendownload dependencies)

#### 3. Jalankan Aplikasi

**Opsi A — Menggunakan Emulator:**
1. Buka **Device Manager** (ikon ponsel di toolbar kanan)
2. Klik **Create Virtual Device**
3. Pilih device (contoh: Pixel 6) → pilih system image (min. API 24) → Finish
4. Klik tombol **Run ** (Shift+F10)

**Opsi B — Menggunakan Perangkat Fisik:**
1. Aktifkan **Developer Options** di HP:
   - Buka **Settings → About Phone** → tap **Build Number** 7 kali
2. Aktifkan **USB Debugging**:
   - Buka **Settings → Developer Options → USB Debugging** → ON
3. Sambungkan HP ke PC via kabel USB
4. Pilih perangkat di dropdown toolbar Android Studio
5. Klik tombol **Run ** (Shift+F10)

#### 4. Build APK (Opsional)

Untuk menghasilkan file APK yang bisa diinstal langsung:

```bash
# Debug APK
./gradlew assembleDebug
```

File APK akan tersedia di:
```
app/build/outputs/apk/debug/app-debug.apk
```

Transfer file ini ke HP dan instal (pastikan **Install from Unknown Sources** diaktifkan).

---

## 📱 Cara Menggunakan Aplikasi

### Tab 1:  Home
- Dashboard utama dengan ringkasan sesi terakhir
- **AI Insight** menampilkan analisis tren latihan

### Tab 2:  AI Coach
- Input kondisi fisik: durasi tidur, nyeri otot, tujuan, performa sebelumnya
- AI Forward Chaining menganalisis 6 rules dan memberikan rekomendasi:
  - **Normal Training** / **Light Training** / **Rest Day**
  - Penyesuaian beban dan volume (%)
  - Confidence score + jejak inferensi lengkap

### Tab 3:  Tracker
- Pilih hari latihan: **Push** / **Pull** / **Legs** (auto-suggest berdasarkan sesi terakhir)
- Lihat daftar gerakan dengan instruksi + video YouTube
- Mulai workout → log set (berat kg, reps, intensitas)
- Rest timer otomatis muncul setelah log set
- Lihat progress chart per exercise

### Tab 4:  Riwayat
- Daftar semua sesi AI Coach yang tersimpan
- Filter berdasarkan tipe rekomendasi

### Tab 5:  Profil
- Ubah nama pengguna dan default goal
- Reset riwayat

---

##  Struktur Proyek

```
app/src/main/java/com/ukdw/pplaicoach/
├── data/
│   ├── local/                  # Room entities + DAOs
│   │   ├── AppDatabase.kt      # Database v2 (5 entities)
│   │   ├── WorkoutSessionEntity.kt  # Sesi AI
│   │   ├── ExerciseEntity.kt        # 9 gerakan PPL (pre-populated)
│   │   ├── TrackerSessionEntity.kt  # Sesi tracker
│   │   └── SetRecordEntity.kt       # Set records (berat/reps)
│   └── repository/
│       ├── WorkoutRepository.kt     # Repo untuk AI Expert System
│       └── TrackerRepository.kt     # Repo untuk Workout Tracker
├── domain/
│   ├── engine/
│   │   └── InferenceEngine.kt  # Forward Chaining (6 rules)
│   └── model/
│       └── Models.kt           # Domain models + enums
├── navigation/
│   ├── Screen.kt               # Route definitions
│   └── NavGraph.kt             # Navigation graph
├── presentation/
│   ├── home/                   # HomeScreen + ViewModel
│   ├── input/                  # InputScreen (form kondisi fisik)
│   ├── result/                 # ResultScreen (hasil inferensi AI)
│   ├── history/                # HistoryScreen + ViewModel
│   ├── profile/                # ProfileScreen + ViewModel
│   ├── test/                   # TestModeScreen (hidden)
│   └── tracker/                # ★ BARU: Workout Tracker
│       ├── TrackerScreen.kt    # PPL day selector + exercise cards
│       ├── TrackerViewModel.kt # Auto-suggest + session + timer
│       ├── RestTimerComposable.kt  # Circular countdown timer
│       ├── ProgressScreen.kt   # Vico chart + stats
│       └── ProgressViewModel.kt
├── ui/theme/                   # Material 3 theme + colors
├── MainActivity.kt             # Single-activity (5-tab bottom nav)
└── PPLAICoachApp.kt            # Application class (singletons)
```

---

##  Testing

```bash
# Jalankan unit tests (InferenceEngine)
./gradlew test

# Jalankan instrumented tests
./gradlew connectedAndroidTest
```

**TestMode** tersembunyi dapat diakses dari halaman Profil untuk memvalidasi 5 skenario inferensi AI (T1–T5).

---


