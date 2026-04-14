package com.ailyn.finanzasana.features.auth.data.datasource

import com.ailyn.finanzasana.features.auth.data.model.*

interface AuthDataSource {
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun registrar(request: UsuarioRequest): UsuarioResponse
}