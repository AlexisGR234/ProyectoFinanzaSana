package com.ailyn.finanzasana.features.home.domain.repository

import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import com.ailyn.finanzasana.features.home.domain.model.Abono
import com.ailyn.finanzasana.features.home.domain.model.Deuda

/**
 * Interfaz del Repositorio de Deudas.
 * Define las operaciones que la capa de Presentación puede solicitar.
 */
interface DeudaRepository {

    // Obtener la lista de todas las deudas del usuario
    suspend fun getDeudas(): Result<List<Deuda>>

    // Obtener el detalle completo de una deuda (incluyendo sus abonos)
    suspend fun getDeudaById(idDeuda: Int): Result<Deuda>

    // Registrar un nuevo pago (abono) a una deuda específica
    suspend fun registrarAbono(idDeuda: Int, monto: Double): Result<Abono>

    // Crear una nueva deuda desde la App
    suspend fun registrarDeuda(request: DeudaRequest): Result<Deuda>

    // Actualizar una deuda existente
    suspend fun actualizarDeuda(idDeuda: Int, request: DeudaRequest): Result<Boolean>

    // Eliminar una deuda existente
    suspend fun eliminarDeuda(idDeuda: Int): Result<Boolean>
}