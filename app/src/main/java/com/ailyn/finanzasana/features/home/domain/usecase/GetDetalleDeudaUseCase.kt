package com.ailyn.finanzasana.features.home.domain.usecase

import com.ailyn.finanzasana.features.home.domain.model.Deuda
import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener la información detallada de una deuda.
 * Se identifica fácilmente como "Obtener Detalle Deuda".
 */
class GetDetalleDeudaUseCase @Inject constructor(
    private val repository: DeudaRepository
) {
    /**
     * @param idDeuda El ID de la deuda que queremos consultar.
     */
    suspend operator fun invoke(idDeuda: Int): Result<Deuda> {
        return repository.getDeudaById(idDeuda)
    }
}