package com.ailyn.finanzasana.features.planificador.data.model

import com.ailyn.finanzasana.features.home.data.model.DeudaResponse
import kotlinx.serialization.Serializable

@Serializable
data class PlanificadorResponse(
    val metodo: String,
    val totalDeuda: Double,
    val tasaPromedio: Double,
    val deudasOrdenadas: List<DeudaResponse>
)