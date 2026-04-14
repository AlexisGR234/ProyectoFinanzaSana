package com.ailyn.finanzasana.features.planificador.data.datasource

import com.ailyn.finanzasana.features.planificador.data.model.PlanificadorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

interface PlanificadorDataSource {
    suspend fun getPlan(metodo: String): PlanificadorResponse
}

class PlanificadorDataSourceImpl @Inject constructor(
    private val client: HttpClient
) : PlanificadorDataSource {

    override suspend fun getPlan(metodo: String): PlanificadorResponse {
        return client.get("planificador/$metodo") {
            contentType(ContentType.Application.Json)
            // Aquí iría el token si ya lo tienes guardado en un Preference o DataStore
            // header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }
}