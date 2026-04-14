package com.ailyn.finanzasana.features.planificador.domain.usecase

import com.ailyn.finanzasana.features.planificador.domain.model.PlanificadorResultado
import com.ailyn.finanzasana.features.planificador.domain.repository.PlanificadorRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener la estrategia de pago (Bola de Nieve o Avalancha).
 * Se comunica con el repositorio para traer los datos procesados por la API.
 */
class GenerarPlanificadorUseCase @Inject constructor(
    private val repository: PlanificadorRepository
) {
    /**
     * @param metodo El nombre de la estrategia ("Bola de Nieve" o "Avalancha").
     */
    suspend operator fun invoke(metodo: String): Result<PlanificadorResultado> {
        return repository.obtenerPlan(metodo)
    }
}