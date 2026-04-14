package com.ailyn.finanzasana

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ailyn.finanzasana.core.navigation.AppNavHost
import com.ailyn.finanzasana.ui.theme.FinanzaSanaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanzaSanaTheme {
                AppNavHost()
            }
        }
    }
}
