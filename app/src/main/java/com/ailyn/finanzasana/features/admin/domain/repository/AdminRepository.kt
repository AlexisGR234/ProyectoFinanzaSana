package com.ailyn.finanzasana.features.admin.domain.repository

import com.ailyn.finanzasana.features.admin.data.model.AdminMetricsResponse
import com.ailyn.finanzasana.features.admin.data.model.ActividadAdminResponse
import com.ailyn.finanzasana.features.admin.data.model.UserAdminResponse

interface AdminRepository {
    // Usamos los nombres que definimos en el DataSource para que todo encaje
    suspend fun getMetrics(): Result<AdminMetricsResponse>
    suspend fun getRecentActivity(): Result<List<ActividadAdminResponse>>
    suspend fun getUsers(): Result<List<UserAdminResponse>>
    suspend fun deleteUser(id: Int): Result<Unit>
}