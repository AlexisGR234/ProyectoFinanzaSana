package com.ailyn.finanzasana.features.home.data.datasource

import com.ailyn.finanzasana.features.home.data.model.AbonoResponse
import com.ailyn.finanzasana.features.home.data.model.DeudaResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class DeudaDataSourceImpl @Inject constructor(
    private val client: HttpClient
    // Eliminamos SessionManager de aquí porque el NetworkModule ya se encarga del token
) : DeudaDataSource {

    override suspend fun getDeudas(): List<DeudaResponse> {
        return client.get("deudas") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getDeudaById(idDeuda: Int): DeudaResponse? {
        return try {
            client.get("deudas/$idDeuda") {
                contentType(ContentType.Application.Json)
            }.body()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun registrarAbono(idDeuda: Int, monto: Double): AbonoResponse? {
        return try {
            client.post("abonos/$idDeuda") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("monto" to monto))
            }.body()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun registrarDeuda(request: com.ailyn.finanzasana.features.home.data.model.DeudaRequest): DeudaResponse? {
        return try {
            client.post("deudas") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun actualizarDeuda(idDeuda: Int, request: com.ailyn.finanzasana.features.home.data.model.DeudaRequest): Boolean {
        return try {
            val response = client.put("deudas/$idDeuda") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun eliminarDeuda(idDeuda: Int): Boolean {
        return try {
            val response = client.delete("deudas/$idDeuda")
            response.status.value in 200..299
        } catch (e: Exception) {
            false
        }
    }
}