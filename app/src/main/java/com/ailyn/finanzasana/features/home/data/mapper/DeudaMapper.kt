package com.ailyn.finanzasana.features.home.data.mapper

import com.ailyn.finanzasana.features.home.data.model.DeudaResponse
import com.ailyn.finanzasana.features.home.data.model.AbonoResponse
import com.ailyn.finanzasana.features.home.domain.model.Deuda
import com.ailyn.finanzasana.features.home.domain.model.Abono


// --- Mappers de Abono ---

/**
 * Convierte un DTO de Abono a modelo de Dominio.
 */
fun AbonoResponse.toDomain(): Abono {
    return Abono(
        id = this.id,
        monto = this.monto,
        fecha = this.fecha
    )
}

/**
 * Convierte una lista de DTOs de Abono a lista de Dominio.
 */
fun List<AbonoResponse>.toDomainList(): List<Abono> = this.map { it.toDomain() }


// --- Mappers de Deuda ---

/**
 * Convierte un DTO de Deuda a modelo de Dominio.
 * También mapea automáticamente la lista de abonos incluida.
 */
fun DeudaResponse.toDomain(): Deuda {
    return Deuda(
        id = this.id,
        concepto = this.concepto,
        montoOriginal = this.montoOriginal,
        saldoActual = this.saldoActual,
        porcentajePagado = this.porcentajePagado,
        tasaInteres = this.tasaInteres,
        fechaVencimiento = this.fechaVencimiento,
        categoria = this.categoria,
        fotoBase64 = this.fotoBase64,
        latitud = this.latitud,
        longitud = this.longitud,
        abonos = this.abonos.toDomainList() // Mapeo anidado
    )
}

/**
 * Convierte una lista de DTOs de Deuda a lista de Dominio.
 * Útil para la pantalla principal de DeudasScreen.
 */
fun List<DeudaResponse>.toDomainListDeudas(): List<Deuda> = this.map { it.toDomain() }