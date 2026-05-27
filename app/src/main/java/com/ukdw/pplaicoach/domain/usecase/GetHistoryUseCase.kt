package com.ukdw.pplaicoach.domain.usecase

import com.ukdw.pplaicoach.data.local.WorkoutSessionEntity
import com.ukdw.pplaicoach.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use Case: Mengambil Riwayat Sesi Latihan
 * Menyediakan akses ke seluruh riwayat sesi latihan dari database lokal.
 */
class GetHistoryUseCase(
    private val repository: WorkoutRepository
) {
    /**
     * @return Flow yang memancarkan daftar seluruh sesi latihan (terbaru di atas)
     */
    operator fun invoke(): Flow<List<WorkoutSessionEntity>> {
        return repository.getAllSessions()
    }
}
