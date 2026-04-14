package com.ailyn.finanzasana.features.auth.domain.usecase

import com.ailyn.finanzasana.core.session.SessionManager
import com.ailyn.finanzasana.features.auth.domain.model.User
import com.ailyn.finanzasana.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // 1. Intentamos el login
        val result = repository.login(email, password)

        // 2. Si el resultado es exitoso, guardamos el token y rol en SessionManager
        result.onSuccess { user ->
            if (user.token.isNotEmpty()) {
                sessionManager.saveSession(user.token, user.rol)
            }
        }

        // 3. Devolvemos el resultado (éxito o error) al ViewModel
        return result
    }
}