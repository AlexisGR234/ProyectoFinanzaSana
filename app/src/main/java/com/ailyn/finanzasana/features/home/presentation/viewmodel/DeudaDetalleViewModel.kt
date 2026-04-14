package com.ailyn.finanzasana.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ailyn.finanzasana.features.home.domain.usecase.GetDetalleDeudaUseCase
import com.ailyn.finanzasana.features.home.domain.usecase.RegistrarAbonoUseCase
import com.ailyn.finanzasana.features.home.domain.usecase.ActualizarDeudaUseCase
import com.ailyn.finanzasana.features.home.domain.usecase.EliminarDeudaUseCase
import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleDeudaViewModel @Inject constructor(
    private val getDetalleDeudaUseCase: GetDetalleDeudaUseCase,
    private val registrarAbonoUseCase: RegistrarAbonoUseCase,
    private val actualizarDeudaUseCase: ActualizarDeudaUseCase,
    private val eliminarDeudaUseCase: EliminarDeudaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleDeudaUiState>(DetalleDeudaUiState.Loading)
    val uiState: StateFlow<DetalleDeudaUiState> = _uiState.asStateFlow()

    /**
     * Carga la información de la deuda desde la API
     */
    fun cargarDetalle(idDeuda: Int) {
        viewModelScope.launch {
            _uiState.value = DetalleDeudaUiState.Loading
            val result = getDetalleDeudaUseCase(idDeuda)

            result.onSuccess { deuda ->
                _uiState.value = DetalleDeudaUiState.Success(deuda)
            }.onFailure { error ->
                _uiState.value = DetalleDeudaUiState.Error(error.message ?: "No se pudo cargar la deuda")
            }
        }
    }

    /**
     * Registra un abono y recarga la información para ver el saldo actualizado
     */
    fun registrarAbono(idDeuda: Int, monto: Double) {
        if (monto <= 0) return

        viewModelScope.launch {
            val result = registrarAbonoUseCase(idDeuda, monto)

            result.onSuccess {
                // Si el abono fue exitoso, recargamos el detalle para ver el nuevo saldo
                cargarDetalle(idDeuda)
            }.onFailure { error ->
                _uiState.value = DetalleDeudaUiState.Error("Error al abonar: ${error.message}")
            }
        }
    }

    fun actualizarDeuda(idDeuda: Int, request: DeudaRequest) {
        viewModelScope.launch {
            _uiState.value = DetalleDeudaUiState.Loading
            val result = actualizarDeudaUseCase(idDeuda, request)
            
            result.onSuccess {
                cargarDetalle(idDeuda)
            }.onFailure { error ->
                _uiState.value = DetalleDeudaUiState.Error("Error al actualizar la deuda: ${error.message}")
            }
        }
    }

    fun eliminarDeuda(idDeuda: Int) {
        viewModelScope.launch {
            _uiState.value = DetalleDeudaUiState.Loading
            val result = eliminarDeudaUseCase(idDeuda)
            
            result.onSuccess {
                _uiState.value = DetalleDeudaUiState.Deleted
            }.onFailure { error ->
                _uiState.value = DetalleDeudaUiState.Error("Error al eliminar la deuda: ${error.message}")
            }
        }
    }
}