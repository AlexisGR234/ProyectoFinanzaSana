package com.ailyn.finanzasana.features.planificador.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailyn.finanzasana.features.planificador.domain.usecase.GenerarPlanificadorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanificadorViewModel @Inject constructor(
    private val generarPlanificadorUseCase: GenerarPlanificadorUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlanificadorUiState>(PlanificadorUiState.Loading)
    val uiState: StateFlow<PlanificadorUiState> = _uiState.asStateFlow()

    // Guardamos el método actual para poder refrescar si es necesario
    private var metodoActual: String = "Bola de Nieve"

    init {
        // Carga inicial por defecto
        cambiarMetodo("Bola de Nieve")
    }

    /**
     * Llama a la API cada vez que el usuario cambia de estrategia
     */
    fun cambiarMetodo(nuevoMetodo: String) {
        metodoActual = nuevoMetodo
        viewModelScope.launch {
            _uiState.value = PlanificadorUiState.Loading

            val result = generarPlanificadorUseCase(nuevoMetodo)

            result.onSuccess { resultado ->
                _uiState.value = PlanificadorUiState.Success(resultado)
            }.onFailure { error ->
                _uiState.value = PlanificadorUiState.Error(
                    error.message ?: "No se pudo generar el plan"
                )
            }
        }
    }

    // Función para refrescar los datos actuales
    fun refrescar() {
        cambiarMetodo(metodoActual)
    }
}