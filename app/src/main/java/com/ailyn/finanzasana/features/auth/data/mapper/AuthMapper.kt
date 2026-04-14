package com.ailyn.finanzasana.features.auth.data.mapper

import com.ailyn.finanzasana.features.auth.data.model.LoginResponse
import com.ailyn.finanzasana.features.auth.data.model.UsuarioResponse
import com.ailyn.finanzasana.features.auth.domain.model.User

fun LoginResponse.toDomain(): User {
    return User(
        id = this.usuario.id,
        nombre = this.usuario.nombre,
        email = this.usuario.email,
        token = this.token,
        rol = this.usuario.rol
    )
}

fun UsuarioResponse.toDomain(): User {
    return User(
        id = this.id,
        nombre = this.nombre,
        email = this.email,
        token = "", // El registro no suele devolver token de inmediato
        rol = this.rol
    )
}