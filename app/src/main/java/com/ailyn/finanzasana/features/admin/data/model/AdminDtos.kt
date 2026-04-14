package com.ailyn.finanzasana.features.admin.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminMetricsResponse(
    val usuariosTotales: Int = 0,
    val montoGlobal: Double = 0.0,
    val deudasVencidas: Int = 0
)

@Serializable
data class ActividadAdminResponse(
    val usuario: String,
    val accion: String,
    val fecha: String
)

@Serializable
data class UserAdminResponse(
    val id: Int, // <-- ESTO ES LO QUE FALTA AQUÍ
    val nombre: String,
    val email: String,
    val totalDeudas: Int = 0
)