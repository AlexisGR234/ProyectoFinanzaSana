package com.ailyn.finanzasana.features.planificador.data.repository

import com.ailyn.finanzasana.features.planificador.data.datasource.PlanificadorDataSource
import com.ailyn.finanzasana.features.planificador.data.mapper.toDomain
import com.ailyn.finanzasana.features.planificador.domain.model.PlanificadorResultado
import com.ailyn.finanzasana.features.planificador.domain.repository.PlanificadorRepository
import javax.inject.Inject

class PlanificadorRepositoryImpl @Inject constructor(
    private val dataSource: PlanificadorDataSource
) : PlanificadorRepository {

    override suspend fun obtenerPlan(metodo: String): Result<PlanificadorResultado> {
        return try {
            val response = dataSource.getPlan(metodo)
            // Convertimos el DTO de la API al modelo de Dominio
            Result.success(response.toDomain())
        } catch (e: Exception) {
            // Si algo falla, devolvemos el error encapsulado
            Result.failure(e)
        }
    }
}