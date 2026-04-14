package com.ailyn.finanzasana.features.admin.domain.model
data class AdminMetrics(
    val usuariosTotales: Int,
    val montoGlobal: Double,
    val deudasVencidas: Int
)