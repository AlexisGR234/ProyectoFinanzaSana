package com.ailyn.finanzasana.features.home.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeudaRequest(
    val concepto: String,
    val montoOriginal: Double,
    val tasaInteres: Double?,
    val idCategoria: Int,
    val fechaVencimiento: String,
    val fotoBase64: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)