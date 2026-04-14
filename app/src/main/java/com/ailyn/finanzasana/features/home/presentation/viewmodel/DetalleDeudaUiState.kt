package com.ailyn.finanzasana.features.home.presentation.viewmodel

import com.ailyn.finanzasana.features.home.domain.model.Deuda

sealed interface DetalleDeudaUiState {
    object Loading : DetalleDeudaUiState
    data class Success(val deuda: Deuda) : DetalleDeudaUiState
    data class Error(val message: String) : DetalleDeudaUiState
    object Deleted : DetalleDeudaUiState
}