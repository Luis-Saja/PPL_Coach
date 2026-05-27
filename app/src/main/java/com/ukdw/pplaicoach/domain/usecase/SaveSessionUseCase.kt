package com.ukdw.pplaicoach.domain.usecase

import com.ukdw.pplaicoach.data.repository.WorkoutRepository
import com.ukdw.pplaicoach.domain.model.InferenceResult
import com.ukdw.pplaicoach.domain.model.UserInput

/**
 * Use Case: Menyimpan Sesi Latihan
 * Menyimpan data input pengguna beserta hasil inferensi ke database lokal (Room).
 */
class SaveSessionUseCase(
    private val repository: WorkoutRepository
) {
    /**
     * @param input Data kondisi fisik yang dimasukkan pengguna
     * @param result Hasil inferensi dari Inference Engine
     */
    suspend operator fun invoke(input: UserInput, result: InferenceResult) {
        repository.saveSession(input, result)
    }
}
