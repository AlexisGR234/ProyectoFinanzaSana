package com.ailyn.finanzasana.features.home.domain.usecase

import com.ailyn.finanzasana.features.home.data.datasource.CategoriaDataSource
import com.ailyn.finanzasana.features.home.data.model.CategoriaResponse
import javax.inject.Inject

/**
 * Obtiene la lista de categorías disponibles desde la API.
 */
class GetCategoriasUseCase @Inject constructor(
    private val categoriaDataSource: CategoriaDataSource
) {
    suspend operator fun invoke(): Result<List<CategoriaResponse>> {
        return try {
            val categorias = categoriaDataSource.getCategorias()
            Result.success(categorias)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
