package com.ukdw.pplaicoach.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ukdw.pplaicoach.domain.model.UserGoal
import com.ukdw.pplaicoach.ui.theme.*

/**
 * === PROFILE SCREEN ===
 *
 * Layar profil dan pengaturan pengguna PPL AI Coach.
 * Fitur:
 * - Input nama pengguna
 * - Toggle tujuan latihan default (Bulking/Cutting)
 * - Tombol reset seluruh riwayat data dengan dialog konfirmasi
 * - Kartu pesan anti-dependency (disclaimer)
 * - Seksi tentang aplikasi (nama, versi, tim, privasi)
 * - Easter egg: ketuk logo/nama app 5x untuk masuk Test Mode
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userName: String,
    defaultGoal: UserGoal,
    onUserNameChange: (String) -> Unit,
    onDefaultGoalChange: (UserGoal) -> Unit,
    onResetHistory: () -> Unit,
    onOpenTestMode: () -> Unit
) {
    // State untuk dialog konfirmasi reset riwayat
    var showResetDialog by remember { mutableStateOf(false) }

    // State untuk penghitung ketukan logo (easter egg test mode)
    var logoTapCount by remember { mutableIntStateOf(0) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ========================
        // HEADER
        // ========================
        Text(
            text = "👤 Profil & Pengaturan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
        )

        // ========================
        // SEKSI: INPUT NAMA PENGGUNA
        // Menggunakan OutlinedTextField agar konsisten dengan Material 3
        // ========================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nama Pengguna",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    label = { Text("Masukkan nama kamu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        cursorColor = PrimaryBlue
                    )
                )
            }
        }

        // ========================
        // SEKSI: TOGGLE TUJUAN LATIHAN DEFAULT
        // Menggunakan SegmentedButton untuk pilihan Bulking/Cutting
        // ========================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tujuan Latihan Default",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Pilihan ini akan otomatis terpilih saat mengisi form input harian.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )

                // SegmentedButton untuk Bulking / Cutting
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserGoal.entries.forEachIndexed { index, goal ->
                        SegmentedButton(
                            selected = defaultGoal == goal,
                            onClick = { onDefaultGoalChange(goal) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = UserGoal.entries.size
                            ),
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = PrimaryBlue,
                                activeContentColor = OnPrimary
                            )
                        ) {
                            Text(text = goal.displayName)
                        }
                    }
                }
            }
        }

        // ========================
        // SEKSI: RESET RIWAYAT DATA
        // Tombol untuk menghapus seluruh data sesi dari Room DB
        // ========================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = RestDayRedLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = RestDayRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reset Riwayat Data",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = RestDayRed
                    )
                }

                Text(
                    text = "Menghapus seluruh data sesi latihan yang tersimpan. " +
                            "Tindakan ini tidak dapat dibatalkan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )

                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RestDayRed,
                        contentColor = OnPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Semua Data")
                }
            }
        }

        // ========================
        // KARTU: PESAN ANTI-DEPENDENCY
        // Disclaimer bahwa aplikasi bukan pengganti pelatih profesional
        // ========================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = LightTrainingYellowLight
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = LightTrainingYellow,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "PPL AI Coach adalah asisten pendukung, " +
                            "bukan pengganti pelatih profesional.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface
                )
            }
        }

        // ========================
        // SEKSI: TENTANG APLIKASI
        // Informasi aplikasi, versi, tim pengembang, dan kebijakan privasi
        // ========================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tentang Aplikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                HorizontalDivider(color = DividerColor)

                // Nama aplikasi — dapat diketuk 5x untuk masuk Test Mode (easter egg)
                Text(
                    text = "🏋️ PPL AI Coach",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Tanpa efek ripple agar easter egg tersembunyi
                        ) {
                            logoTapCount++
                            if (logoTapCount >= 5) {
                                logoTapCount = 0
                                onOpenTestMode()
                            }
                        }
                )

                // Informasi versi
                AboutInfoRow(label = "Versi", value = "1.0.0")

                // Informasi tim pengembang
                AboutInfoRow(
                    label = "Tim",
                    value = "TI0263 – Kecerdasan Buatan, UKDW"
                )

                HorizontalDivider(color = DividerColor)

                // Kebijakan privasi
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NormalTrainingGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Semua data disimpan lokal (Room DB), " +
                                "tidak dikirim ke server manapun.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        // Spacer di akhir agar tidak terpotong oleh navigation bar
        Spacer(modifier = Modifier.height(32.dp))
    }

    // ========================
    // DIALOG KONFIRMASI RESET RIWAYAT
    // AlertDialog untuk memastikan pengguna sadar akan tindakan penghapusan
    // ========================
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = RestDayRed
                )
            },
            title = {
                Text(
                    text = "Reset Riwayat Data?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Semua data sesi latihan akan dihapus secara permanen. " +
                            "Tindakan ini tidak dapat dibatalkan.\n\n" +
                            "Apakah kamu yakin ingin melanjutkan?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetHistory()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RestDayRed,
                        contentColor = OnPrimary
                    )
                ) {
                    Text("Ya, Hapus Semua")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

/**
 * Komponen helper untuk menampilkan baris informasi (label: value)
 * di seksi Tentang Aplikasi.
 */
@Composable
private fun AboutInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.7f)
        )
    }
}
