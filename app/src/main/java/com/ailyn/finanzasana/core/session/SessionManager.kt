package com.ailyn.finanzasana.core.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Instancia única de DataStore para la sesión
private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val ROL_KEY = androidx.datastore.preferences.core.intPreferencesKey("user_rol")
    }

    // Guarda el token y el rol cuando el login es exitoso
    suspend fun saveSession(token: String, rol: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[ROL_KEY] = rol
        }
    }

    // Obtiene el token como un Flow para el NetworkModule
    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // Obtiene el rol como un Flow para la capa de presentación
    val userRol: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[ROL_KEY]
    }

    // Limpia la sesión al cerrar sesión (Cerrar Sesión)
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(ROL_KEY)
        }
    }
}