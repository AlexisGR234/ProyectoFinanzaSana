package com.ailyn.finanzasana.features.home.domain.usecase

import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import javax.inject.Inject

class ActualizarDeudaUseCase @Inject constructor(
    private val repository: DeudaRepository
) {
    suspend operator fun invoke(idDeuda: Int, request: DeudaRequest): Result<Boolean> {
        return repository.actualizarDeuda(idDeuda, request)
    }
}
