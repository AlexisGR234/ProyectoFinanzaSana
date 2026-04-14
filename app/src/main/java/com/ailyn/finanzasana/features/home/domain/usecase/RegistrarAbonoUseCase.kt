package com.ailyn.finanzasana.features.home.domain.usecase

import com.ailyn.finanzasana.features.home.domain.model.Abono
import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import javax.inject.Inject

/**
 * Caso de uso para registrar un nuevo pago (abono) a una deuda.
 * Nombre en español para identificarlo fácilmente como "Registrar Abono".
 */
class RegistrarAbonoUseCase @Inject constructor(
    private val repository: DeudaRepository
) {
    /**
     * @param idDeuda El ID de la deuda a la que se le aplicará el pago.
     * @param monto La cantidad de dinero a abonar.
     */
    suspend operator fun invoke(idDeuda: Int, monto: Double): Result<Abono> {
        return repository.registrarAbono(idDeuda, monto)
    }
}