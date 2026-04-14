package com.ailyn.finanzasana.features.home.domain.model

data class Deuda(
    val id: Int,
    val concepto: String,
    val montoOriginal: Double,
    val saldoActual: Double,
    val porcentajePagado: Double,
    val tasaInteres: Double?,
    val fechaVencimiento: String,
    val categoria: String,
    val fotoBase64: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val abonos: List<Abono>
)

data class Abono(
    val id: Int,
    val monto: Double,
    val fecha: String
)