package com.ailyn.finanzasana.features.home.data.datasource

import com.ailyn.finanzasana.features.home.data.model.CategoriaResponse

/**
 * Interfaz que define las operaciones de red para el módulo de Categorías.
 */
interface CategoriaDataSource {
    suspend fun getCategorias(): List<CategoriaResponse>
}
