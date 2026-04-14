package com.ailyn.finanzasana.features.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    @SerialName("contrasena")
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)

@Serializable
data class UsuarioRequest(
    val nombre: String,
    val email: String,
    @SerialName("contrasena")
    val password: String,
    @SerialName("idRol") // En el Request de tu API es idRol
    val idRol: Int,
    val telefono: String
)

@Serializable
data class UsuarioResponse(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: Int, // En el Response de tu API es rol
    val telefono: String
)