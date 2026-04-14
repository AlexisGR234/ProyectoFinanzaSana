package com.ailyn.finanzasana.features.admin.data.datasource

import com.ailyn.finanzasana.features.admin.data.model.AdminMetricsResponse
import com.ailyn.finanzasana.features.admin.data.model.ActividadAdminResponse
import com.ailyn.finanzasana.features.admin.data.model.UserAdminResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import javax.inject.Inject

class AdminDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient
) : AdminDataSource {

    override suspend fun getMetrics(): AdminMetricsResponse {
        // Asegúrate de que en Ktor la ruta sea "admin/metrics"
        return httpClient.get("admin/metrics").body()
    }

    override suspend fun getRecentActivity(): List<ActividadAdminResponse> {
        // Cambiado para coincidir con la interfaz
        return httpClient.get("admin/actividad").body()
    }

    override suspend fun getUsers(): List<UserAdminResponse> {
        return httpClient.get("admin/usuarios").body()
    }

    override suspend fun deleteUser(id: Int) {
        httpClient.delete("usuarios/$id")
    }
}