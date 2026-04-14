package com.ailyn.finanzasana.features.admin.domain.usecase

import com.ailyn.finanzasana.features.admin.data.model.UserAdminResponse
import com.ailyn.finanzasana.features.admin.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminUsersUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(): Result<List<UserAdminResponse>> {
        // Cambiado de getUsuarios() a getUsers() para que coincida con tu repositorio
        return repository.getUsers()
    }
}