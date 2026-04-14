package com.ailyn.finanzasana.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailyn.finanzasana.features.auth.domain.usecase.LoginUseCase
import com.ailyn.finanzasana.features.auth.domain.usecase.RegistrarUsuarioUseCase
import com.ailyn.finanzasana.core.session.SessionManager // Verifica que esta sea tu ruta real del SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase,
    private val sessionManager: SessionManager // <-- 1. Inyectamos el SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // --- FUNCIÓN LOGOUT (Añadida para corregir el error) ---
    // --- FUNCIÓN LOGOUT CORREGIDA ---
    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession() // Ahora coincide con el nombre en tu SessionManager
            _uiState.value = AuthUiState.Idle
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginUseCase(email, password)

            result.onSuccess { user ->
                // Persistimos el token y el rol para que la app pueda usarlos en toda la sesión
                sessionManager.saveSession(user.token, user.rol)
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(
                    error.message ?: "Error al iniciar sesión. Verifica tus credenciales."
                )
            }
        }
    }

    fun registrar(nombre: String, email: String, password: String, telefono: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = registrarUsuarioUseCase(nombre, email, password, telefono)

            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
            }.onFailure { error ->
                _uiState.value = AuthUiState.Error(
                    error.message ?: "No se pudo crear la cuenta. Intenta de nuevo."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}