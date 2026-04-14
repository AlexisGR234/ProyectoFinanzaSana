package com.ailyn.finanzasana.features.auth.di

import com.ailyn.finanzasana.features.auth.data.datasource.AuthDataSource
import com.ailyn.finanzasana.features.auth.data.datasource.AuthDataSourceImpl
import com.ailyn.finanzasana.features.auth.data.repository.AuthRepositoryImpl
import com.ailyn.finanzasana.features.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn // Importe corregido
import dagger.hilt.components.SingletonComponent // Importe corregido
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(httpClient: HttpClient): AuthDataSource {
        // Retornamos la implementación real, no la interfaz
        return AuthDataSourceImpl(httpClient)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(dataSource: AuthDataSource): AuthRepository {
        return AuthRepositoryImpl(dataSource)
    }
}