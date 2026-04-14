package com.ailyn.finanzasana.features.home.domain.usecase

import com.ailyn.finanzasana.features.home.domain.model.Deuda
import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de deudas del usuario.
 * Representa una acción específica de la lógica de negocio.
 */
class GetDeudasUseCase @Inject constructor(
    private val repository: DeudaRepository
) {
    /**
     * Al usar 'operator fun invoke', podemos llamar al caso de uso
     * como si fuera una función: getDeudasUseCase()
     */
    suspend operator fun invoke(): Result<List<Deuda>> {
        return repository.getDeudas()
    }
}