package com.ailyn.finanzasana.features.planificador.domain.repository

import com.ailyn.finanzasana.features.planificador.domain.model.PlanificadorResultado

interface PlanificadorRepository {
    suspend fun obtenerPlan(metodo: String): Result<PlanificadorResultado>
}