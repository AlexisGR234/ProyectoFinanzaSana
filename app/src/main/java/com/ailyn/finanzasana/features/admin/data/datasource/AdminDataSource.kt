package com.ailyn.finanzasana.features.admin.data.datasource

import com.ailyn.finanzasana.features.admin.data.model.AdminMetricsResponse
import com.ailyn.finanzasana.features.admin.data.model.ActividadAdminResponse
import com.ailyn.finanzasana.features.admin.data.model.UserAdminResponse

interface AdminDataSource {
    suspend fun getMetrics(): AdminMetricsResponse
    suspend fun getRecentActivity(): List<ActividadAdminResponse> // Cambiado a getRecentActivity si así lo pide tu API
    suspend fun getUsers(): List<UserAdminResponse> // Cambiado a getUsers para ser consistentes con el plural
    suspend fun deleteUser(id: Int)
}