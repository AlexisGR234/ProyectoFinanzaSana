package com.ailyn.finanzasana.features.auth.domain.model

data class User(
    val id: Int,
    val nombre: String,
    val email: String,
    val token: String,
    val rol: Int
)