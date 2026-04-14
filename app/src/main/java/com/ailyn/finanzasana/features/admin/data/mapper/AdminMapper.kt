package com.ailyn.finanzasana.features.admin.data.mapper

// Cambia 'remote.dto' por 'model' si así están tus carpetas
import com.ailyn.finanzasana.features.admin.data.model.AdminMetricsResponse
import com.ailyn.finanzasana.features.admin.data.model.ActividadAdminResponse
import com.ailyn.finanzasana.features.admin.data.model.UserAdminResponse
import com.ailyn.finanzasana.features.admin.domain.model.AdminMetrics
import com.ailyn.finanzasana.features.admin.domain.model.ActividadAdmin
import com.ailyn.finanzasana.features.admin.domain.model.UserAdmin

// Mapeo de Métricas
fun AdminMetricsResponse.toDomain(): AdminMetrics {
    return AdminMetrics(
        usuariosTotales = this.usuariosTotales,
        montoGlobal = this.montoGlobal,
        deudasVencidas = this.deudasVencidas
    )
}

// Mapeo de Actividad
fun ActividadAdminResponse.toDomain(): ActividadAdmin {
    return ActividadAdmin(
        usuario = this.usuario,
        accion = this.accion,
        fecha = this.fecha
    )
}

// Mapeo de Usuarios
fun UserAdminResponse.toDomain(): UserAdmin {
    return UserAdmin(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        totalDeudas = this.totalDeudas
    )
}

// Funciones para mapear listas (útil para la actividad y usuarios)
fun List<ActividadAdminResponse>.toDomainList(): List<ActividadAdmin> = this.map { it.toDomain() }
fun List<UserAdminResponse>.toDomainListUsers(): List<UserAdmin> = this.map { it.toDomain() }