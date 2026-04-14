package com.ailyn.finanzasana.features.home.data.datasource

import com.ailyn.finanzasana.features.home.data.model.CategoriaResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class CategoriaDataSourceImpl @Inject constructor(
    private val client: HttpClient
) : CategoriaDataSource {

    override suspend fun getCategorias(): List<CategoriaResponse> {
        return client.get("categorias") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}
