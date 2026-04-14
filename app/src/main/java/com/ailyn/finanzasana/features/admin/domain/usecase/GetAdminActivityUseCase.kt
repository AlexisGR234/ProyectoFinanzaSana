package com.ailyn.finanzasana.features.admin.domain.usecase

import com.ailyn.finanzasana.features.admin.data.model.ActividadAdminResponse
import com.ailyn.finanzasana.features.admin.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminActivityUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<List<ActividadAdminResponse>> {
        // Cambiado de getActividad() a getRecentActivity() para coincidir con el repositorio
        return repository.getRecentActivity()
    }
}