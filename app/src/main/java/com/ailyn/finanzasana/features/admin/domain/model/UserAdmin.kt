package com.ailyn.finanzasana.features.admin.domain.model

data class UserAdmin(
    val id: Int, // <-- ESTO ES LO QUE FALTA
    val nombre: String,
    val email: String,
    val totalDeudas: Int
)