package com.ailyn.finanzasana.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailyn.finanzasana.features.home.data.model.CategoriaResponse
import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import com.ailyn.finanzasana.features.home.domain.usecase.GetCategoriasUseCase
import com.ailyn.finanzasana.features.home.domain.usecase.GetDeudasUseCase
import com.ailyn.finanzasana.features.home.domain.usecase.RegistrarDeudaUseCase
import com.ailyn.finanzasana.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeudasViewModel @Inject constructor(
    private val getDeudasUseCase: GetDeudasUseCase,
    private val registrarDeudaUseCase: RegistrarDeudaUseCase,
    private val getCategoriasUseCase: GetCategoriasUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Evento de un solo disparo para navegar al Login tras cerrar sesión
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    val userRol: StateFlow<Int?> = sessionManager.userRol.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _uiState = MutableStateFlow<DeudasUiState>(DeudasUiState.Loading)
    val uiState: StateFlow<DeudasUiState> = _uiState.asStateFlow()

    private val _categorias = MutableStateFlow<List<CategoriaResponse>>(emptyList())
    val categorias: StateFlow<List<CategoriaResponse>> = _categorias.asStateFlow()

    init {
        cargarDeudas()
        cargarCategorias()
    }

    fun cargarDeudas() {
        viewModelScope.launch {
            _uiState.value = DeudasUiState.Loading
            val result = getDeudasUseCase()
            result.onSuccess { listaDeudas ->
                _uiState.value = DeudasUiState.Success(listaDeudas)
            }.onFailure { error ->
                _uiState.value = DeudasUiState.Error(error.message ?: "Error desconocido")
            }
        }
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            getCategoriasUseCase().onSuccess { lista ->
                _categorias.value = lista
            }
            // Si falla, la lista queda vacía y el formulario mostrará un campo de texto normal
        }
    }

    /**
     * Registra una nueva deuda y recarga la lista automáticamente
     */
    fun registrarNuevaDeuda(request: DeudaRequest) {
        viewModelScope.launch {
            val result = registrarDeudaUseCase(request)

            result.onSuccess {
                // Si se guardó con éxito en Postgres, refrescamos la lista
                cargarDeudas()
            }.onFailure { error ->
                _uiState.value = DeudasUiState.Error("Error al registrar: ${error.message}")
            }
        }
    }

    /**
     * Limpia la sesión del usuario y emite el evento de logout
     */
    fun cerrarSesion() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _logoutEvent.emit(Unit)
        }
    }
}