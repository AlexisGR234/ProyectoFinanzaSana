package com.ailyn.finanzasana.core.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable object Login : Screen()
    @Serializable object Register : Screen()
    @Serializable object AdminDashboard : Screen()
    @Serializable object UserManagement : Screen()

    // --- NUEVAS RUTAS ---

    // Pantalla principal de deudas
    @Serializable object Deudas : Screen()

    // Pantalla de detalle (Recibe el ID)
    @Serializable data class DetalleDeuda(val idDeuda: Int) : Screen()

    // Pantalla del Planificador (Módulo aparte)
    @Serializable object Planificador : Screen()
}