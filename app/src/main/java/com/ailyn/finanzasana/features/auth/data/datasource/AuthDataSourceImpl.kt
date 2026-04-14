package com.ailyn.finanzasana.features.auth.data.datasource

import com.ailyn.finanzasana.features.auth.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun login(request: LoginRequest): LoginResponse {
        return httpClient.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun registrar(request: UsuarioRequest): UsuarioResponse {
        return httpClient.post("usuarios/registro") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}