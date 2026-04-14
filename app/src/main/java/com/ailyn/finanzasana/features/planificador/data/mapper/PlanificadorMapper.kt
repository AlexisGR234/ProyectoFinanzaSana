package com.ailyn.finanzasana.features.planificador.data.mapper

import com.ailyn.finanzasana.features.home.data.mapper.toDomainListDeudas
import com.ailyn.finanzasana.features.planificador.data.model.PlanificadorResponse
import com.ailyn.finanzasana.features.planificador.domain.model.PlanificadorResultado

fun PlanificadorResponse.toDomain(): PlanificadorResultado {
    return PlanificadorResultado(
        metodo = this.metodo,
        totalDeuda = this.totalDeuda,
        tasaPromedio = this.tasaPromedio,
        // Reutilizamos el mapper que ya habías hecho para las deudas
        deudasOrdenadas = this.deudasOrdenadas.toDomainListDeudas()
    )
}