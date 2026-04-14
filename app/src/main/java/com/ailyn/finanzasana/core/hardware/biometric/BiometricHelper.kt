package com.ailyn.finanzasana.core.hardware.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

sealed class BiometricResult {
    data object Success : BiometricResult()
    data object Cancelled : BiometricResult()
    data class Error(val message: String) : BiometricResult()
    data object NotAvailable : BiometricResult()
    data object NoneEnrolled : BiometricResult()
}

object BiometricHelper {

    private const val SUPPORTED_AUTHENTICATORS =
        BiometricManager.Authenticators.BIOMETRIC_WEAK

    fun canAuthenticate(context: Context): BiometricResult {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(SUPPORTED_AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricResult.Success
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                BiometricResult.NotAvailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                BiometricResult.NoneEnrolled
            else -> BiometricResult.NotAvailable
        }
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String = "Autenticación biométrica",
        subtitle: String = "Usa tu huella para iniciar sesión",
        negativeButtonText: String = "Usar contraseña",
        onResult: (BiometricResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    onResult(BiometricResult.Cancelled)
                } else {
                    onResult(BiometricResult.Error(errString.toString()))
                }
            }

            override fun onAuthenticationFailed() {
                // Don't call onResult here — the system retries automatically
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(SUPPORTED_AUTHENTICATORS)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
