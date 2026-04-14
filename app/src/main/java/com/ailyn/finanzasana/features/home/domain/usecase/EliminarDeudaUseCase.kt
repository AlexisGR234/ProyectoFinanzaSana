package com.ailyn.finanzasana.features.home.domain.usecase

import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import javax.inject.Inject

class EliminarDeudaUseCase @Inject constructor(
    private val repository: DeudaRepository
) {
    suspend operator fun invoke(idDeuda: Int): Result<Boolean> {
        return repository.eliminarDeuda(idDeuda)
    }
}
