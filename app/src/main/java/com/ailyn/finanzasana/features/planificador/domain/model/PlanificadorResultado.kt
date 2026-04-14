package com.ailyn.finanzasana.features.planificador.domain.model

import com.ailyn.finanzasana.features.home.domain.model.Deuda

data class PlanificadorResultado(
    val metodo: String,
    val totalDeuda: Double,
    val tasaPromedio: Double,
    val deudasOrdenadas: List<Deuda>
)