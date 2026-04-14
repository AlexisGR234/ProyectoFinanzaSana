package com.ailyn.finanzasana.features.admin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailyn.finanzasana.features.admin.data.mapper.toDomainListUsers
import com.ailyn.finanzasana.features.admin.domain.model.UserAdmin
import com.ailyn.finanzasana.features.admin.domain.usecase.GetAdminUsersUseCase
import com.ailyn.finanzasana.features.admin.domain.usecase.DeleteAdminUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. LA INTERFAZ DEBE ESTAR AQUÍ (Esto quita todos los errores rojos de UiState)
sealed interface UserManagementUiState {
    object Loading : UserManagementUiState
    data class Success(val usuarios: List<UserAdmin>) : UserManagementUiState
    data class Error(val message: String) : UserManagementUiState
}

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val getAdminUsersUseCase: GetAdminUsersUseCase,
    private val deleteAdminUserUseCase: DeleteAdminUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserManagementUiState>(UserManagementUiState.Loading)
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    private var usuariosCompletos: List<UserAdmin> = emptyList()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UserManagementUiState.Loading
            getAdminUsersUseCase().onSuccess { dtos ->
                usuariosCompletos = dtos.toDomainListUsers()
                _uiState.value = UserManagementUiState.Success(usuariosCompletos)
            }.onFailure { exception ->
                _uiState.value = UserManagementUiState.Error(
                    exception.message ?: "No se pudo cargar la lista de usuarios"
                )
            }
        }
    }

    fun filterUsers(query: String) {
        val listaFiltrada = if (query.isEmpty()) {
            usuariosCompletos
        } else {
            usuariosCompletos.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = UserManagementUiState.Success(listaFiltrada)
    }

    fun deleteUser(idUsuario: Int) {
        viewModelScope.launch {
            val result = deleteAdminUserUseCase(idUsuario)
            result.onSuccess {
                // Filtramos la lista local después de que en el back end fue un éxito
                usuariosCompletos = usuariosCompletos.filter { it.id != idUsuario }
                _uiState.value = UserManagementUiState.Success(usuariosCompletos)
            }.onFailure { exception ->
                _uiState.value = UserManagementUiState.Error(
                    exception.message ?: "No se pudo eliminar al usuario"
                )
            }
        }
    }
}