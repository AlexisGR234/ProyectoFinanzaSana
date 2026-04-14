package com.ailyn.finanzasana.features.home.di

import com.ailyn.finanzasana.features.home.data.datasource.CategoriaDataSource
import com.ailyn.finanzasana.features.home.data.datasource.CategoriaDataSourceImpl
import com.ailyn.finanzasana.features.home.data.datasource.DeudaDataSource
import com.ailyn.finanzasana.features.home.data.datasource.DeudaDataSourceImpl
import com.ailyn.finanzasana.features.home.data.repository.DeudaRepositoryImpl
import com.ailyn.finanzasana.features.home.domain.repository.DeudaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    /**
     * Le dice a Hilt que use DeudaDataSourceImpl
     * cada vez que una clase pida un DeudaDataSource.
     */
    @Binds
    @Singleton
    abstract fun bindDeudaDataSource(
        deudaDataSourceImpl: DeudaDataSourceImpl
    ): DeudaDataSource

    /**
     * Le dice a Hilt que use DeudaRepositoryImpl
     * cada vez que un UseCase pida un DeudaRepository.
     */
    @Binds
    @Singleton
    abstract fun bindDeudaRepository(
        deudaRepositoryImpl: DeudaRepositoryImpl
    ): DeudaRepository

    /**
     * Le dice a Hilt que use CategoriaDataSourceImpl
     * cada vez que una clase pida un CategoriaDataSource.
     */
    @Binds
    @Singleton
    abstract fun bindCategoriaDataSource(
        categoriaDataSourceImpl: CategoriaDataSourceImpl
    ): CategoriaDataSource
}