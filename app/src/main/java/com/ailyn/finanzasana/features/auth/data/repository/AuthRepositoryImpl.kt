package com.ailyn.finanzasana.features.auth.data.repository

import com.ailyn.finanzasana.features.auth.data.datasource.AuthDataSource
import com.ailyn.finanzasana.features.auth.data.mapper.toDomain
import com.ailyn.finanzasana.features.auth.data.model.LoginRequest
import com.ailyn.finanzasana.features.auth.data.model.UsuarioRequest
import com.ailyn.finanzasana.features.auth.domain.model.User
import com.ailyn.finanzasana.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return runCatching {
            val response = dataSource.login(LoginRequest(email, password))
            response.toDomain()
        }
    }

    override suspend fun registrar(
        nombre: String,
        email: String,
        password: String,
        telefono: String
    ): Result<User> {
        return runCatching {
            // Enviamos idRol = 2 (Usuario normal) por defecto como pide tu API
            val request = UsuarioRequest(
                nombre = nombre,
                email = email,
                password = password,
                idRol = 2,
                telefono = telefono
            )
            val response = dataSource.registrar(request)
            response.toDomain()
        }
    }
}