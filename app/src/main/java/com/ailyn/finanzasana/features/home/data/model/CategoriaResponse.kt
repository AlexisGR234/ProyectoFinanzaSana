package com.ailyn.finanzasana.features.home.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoriaResponse(
    val id: Int,
    val nombre: String
)
