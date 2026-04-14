package com.ailyn.finanzasana.features.auth.presentation.viewmodel

import com.ailyn.finanzasana.features.auth.domain.model.User

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}