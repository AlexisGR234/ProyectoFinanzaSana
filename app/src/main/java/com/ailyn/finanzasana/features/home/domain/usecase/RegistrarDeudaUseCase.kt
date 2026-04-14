package com.ailyn.finanzasana.features.home.domain.usecase

import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import com.ailyn.finanzasana.features.home.domain.model.Deuda
import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import javax.inject.Inject

/**
 * Caso de uso para registrar una nueva deuda en el sistema.
 * Se encarga de comunicar la intención de guardado al repositorio.
 */
class RegistrarDeudaUseCase @Inject constructor(
    private val repository: DeudaRepository
) {
    /**
     * @param request Objeto DTO con la información de la nueva deuda.
     * @return Result con el modelo de la Deuda creada si fue exitoso.
     */
    suspend operator fun invoke(request: DeudaRequest): Result<Deuda> {
        return repository.registrarDeuda(request)
    }
}