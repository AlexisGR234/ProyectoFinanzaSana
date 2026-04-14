package com.ailyn.finanzasana.features.admin.domain.usecase

import com.ailyn.finanzasana.features.admin.domain.repository.AdminRepository
import javax.inject.Inject

class DeleteAdminUserUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return adminRepository.deleteUser(id)
    }
}
