package com.ailyn.finanzasana.features.home.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeudaResponse(
    val id: Int,
    val concepto: String,
    val montoOriginal: Double,
    val saldoActual: Double,
    val porcentajePagado: Double,
    val tasaInteres: Double? = null,
    val fechaVencimiento: String,
    val categoria: String,
    val fotoBase64: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val abonos: List<AbonoResponse> = emptyList()
)

@Serializable
data class AbonoResponse(
    val id: Int,
    val monto: Double,
    val fecha: String
)