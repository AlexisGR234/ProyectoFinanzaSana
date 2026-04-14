package com.ailyn.finanzasana.core.hardware

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class HardwareManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Ejecuta una vibración corta para confirmar acciones exitosas.
     * Compatible con versiones antiguas y nuevas de Android.
     */
    fun vibrateSuccess() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Aquí resolvemos el error de la API 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Para Android 8.0 o superior (Usa VibrationEffect)
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            // Para Android 7.0 y 7.1 (Método antiguo pero seguro)
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }
}