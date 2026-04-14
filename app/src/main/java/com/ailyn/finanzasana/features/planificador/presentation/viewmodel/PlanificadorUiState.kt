package com.ailyn.finanzasana.features.planificador.presentation.viewmodel

import com.ailyn.finanzasana.features.planificador.domain.model.PlanificadorResultado

sealed interface PlanificadorUiState {
    object Loading : PlanificadorUiState
    data class Success(val resultado: PlanificadorResultado) : PlanificadorUiState
    data class Error(val message: String) : PlanificadorUiState
}