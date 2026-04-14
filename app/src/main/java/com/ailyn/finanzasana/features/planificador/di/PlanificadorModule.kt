package com.ailyn.finanzasana.features.planificador.di

import com.ailyn.finanzasana.features.planificador.data.datasource.PlanificadorDataSource
import com.ailyn.finanzasana.features.planificador.data.datasource.PlanificadorDataSourceImpl
import com.ailyn.finanzasana.features.planificador.data.repository.PlanificadorRepositoryImpl
import com.ailyn.finanzasana.features.planificador.domain.repository.PlanificadorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlanificadorModule {

    /**
     * Vincula la interfaz del DataSource con su implementación real que usa Ktor.
     */
    @Binds
    @Singleton
    abstract fun bindPlanificadorDataSource(
        planificadorDataSourceImpl: PlanificadorDataSourceImpl
    ): PlanificadorDataSource

    /**
     * Vincula la interfaz del Repositorio con la implementación que maneja los Mappers.
     */
    @Binds
    @Singleton
    abstract fun bindPlanificadorRepository(
        planificadorRepositoryImpl: PlanificadorRepositoryImpl
    ): PlanificadorRepository
}