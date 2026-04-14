package com.ailyn.finanzasana.features.home.data.repository

import com.ailyn.finanzasana.features.home.data.datasource.DeudaDataSource
import com.ailyn.finanzasana.features.home.data.mapper.toDomain
import com.ailyn.finanzasana.features.home.data.mapper.toDomainListDeudas
import com.ailyn.finanzasana.features.home.data.model.DeudaRequest
import com.ailyn.finanzasana.features.home.domain.model.Abono
import com.ailyn.finanzasana.features.home.domain.model.Deuda
import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import javax.inject.Inject

class DeudaRepositoryImpl @Inject constructor(
    private val dataSource: DeudaDataSource
) : DeudaRepository {

    override suspend fun getDeudas(): Result<List<Deuda>> {
        return try {
            val response = dataSource.getDeudas()
            Result.success(response.toDomainListDeudas())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeudaById(idDeuda: Int): Result<Deuda> {
        return try {
            val response = dataSource.getDeudaById(idDeuda)
            if (response != null) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception("No se encontró la deuda"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarAbono(idDeuda: Int, monto: Double): Result<Abono> {
        return try {
            val response = dataSource.registrarAbono(idDeuda, monto)
            if (response != null) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception("Error al registrar el abono"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarDeuda(request: DeudaRequest): Result<Deuda> {
        return try {
            val response = dataSource.registrarDeuda(request)

            if (response != null) {
                Result.success(response.toDomain())
            } else {
                Result.failure(Exception("Error al registrar la deuda en el servidor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun actualizarDeuda(idDeuda: Int, request: DeudaRequest): Result<Boolean> {
        return try {
            val exito = dataSource.actualizarDeuda(idDeuda, request)
            Result.success(exito)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun eliminarDeuda(idDeuda: Int): Result<Boolean> {
        return try {
            val exito = dataSource.eliminarDeuda(idDeuda)
            Result.success(exito)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}