package com.ailyn.finanzasana.features.auth.domain.usecase

import com.ailyn.finanzasana.features.auth.domain.model.User
import com.ailyn.finanzasana.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegistrarUsuarioUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        nombre: String,
        email: String,
        password: String,
        telefono: String
    ): Result<User> {
        // Ejecutamos el registro a través del repositorio
        // El idRol (2) ya está definido dentro de la implementación del repositorio
        return repository.registrar(
            nombre = nombre,
            email = email,
            password = password,
            telefono = telefono
        )
    }
}