package com.ailyn.finanzasana.features.admin.domain.usecase

import com.ailyn.finanzasana.features.admin.data.model.AdminMetricsResponse // IMPORT CORREGIDO
import com.ailyn.finanzasana.features.admin.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminMetricsUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<AdminMetricsResponse> {
        return repository.getMetrics()
    }
}