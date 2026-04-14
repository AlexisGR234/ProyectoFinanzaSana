package com.ailyn.finanzasana.features.admin.data.repository

import com.ailyn.finanzasana.features.admin.data.datasource.AdminDataSource
import com.ailyn.finanzasana.features.admin.data.model.AdminMetricsResponse
import com.ailyn.finanzasana.features.admin.data.model.ActividadAdminResponse
import com.ailyn.finanzasana.features.admin.data.model.UserAdminResponse
import com.ailyn.finanzasana.features.admin.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val dataSource: AdminDataSource
) : AdminRepository {

    override suspend fun getMetrics(): Result<AdminMetricsResponse> {
        return runCatching { dataSource.getMetrics() }
    }

    override suspend fun getRecentActivity(): Result<List<ActividadAdminResponse>> {
        return runCatching { dataSource.getRecentActivity() }
    }

    override suspend fun getUsers(): Result<List<UserAdminResponse>> {
        return runCatching { dataSource.getUsers() }
    }

    override suspend fun deleteUser(id: Int): Result<Unit> {
        return runCatching { dataSource.deleteUser(id) }
    }
}