package com.ailyn.finanzasana.features.auth.domain.repository

import com.ailyn.finanzasana.features.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>

    suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        telefono: String
    ): Result<User>
}