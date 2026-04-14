package com.ailyn.finanzasana.features.home.data.datasource

import com.ailyn.finanzasana.features.home.data.model.AbonoResponse
import com.ailyn.finanzasana.features.home.data.model.DeudaResponse

/**
 * Interfaz que define las operaciones de red para el módulo de Deudas.
 * Sigue el principio de Inversión de Dependencias.
 */
interface DeudaDataSource {
    suspend fun getDeudas(): List<DeudaResponse>
    suspend fun getDeudaById(idDeuda: Int): DeudaResponse?
    suspend fun registrarAbono(idDeuda: Int, monto: Double): AbonoResponse?
    suspend fun registrarDeuda(request: com.ailyn.finanzasana.features.home.data.model.DeudaRequest): DeudaResponse?
    suspend fun actualizarDeuda(idDeuda: Int, request: com.ailyn.finanzasana.features.home.data.model.DeudaRequest): Boolean
    suspend fun eliminarDeuda(idDeuda: Int): Boolean
}