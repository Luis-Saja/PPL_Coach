package com.ukdw.pplaicoach.domain.usecase

import com.ukdw.pplaicoach.domain.model.InferenceResult
import com.ukdw.pplaicoach.domain.model.UserInput
import com.ukdw.pplaicoach.domain.rules.InferenceEngine

/**
 * Use Case: Menjalankan Inferensi
 * Menerima input kondisi fisik pengguna dan mengembalikan hasil rekomendasi
 * dari Inference Engine (Forward Chaining).
 */
class RunInferenceUseCase(
    private val inferenceEngine: InferenceEngine = InferenceEngine()
) {
    /**
     * @param input Data kondisi fisik harian pengguna
     * @return Hasil inferensi berisi rekomendasi, adjustment, dan penjelasan
     */
    operator fun invoke(input: UserInput): InferenceResult {
        return inferenceEngine.run(input)
    }
}
