package com.ailyn.finanzasana.features.home.presentation.viewmodel

import com.ailyn.finanzasana.features.home.domain.model.Deuda

sealed interface DeudasUiState {
    object Loading : DeudasUiState
    data class Success(val deudas: List<Deuda>) : DeudasUiState
    data class Error(val message: String) : DeudasUiState
}