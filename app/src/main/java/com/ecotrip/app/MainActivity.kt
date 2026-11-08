package com.ecotrip.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ecotrip.app.navigation.EcoTripNavHost
import com.ecotrip.app.ui.theme.EcoTripTheme

/** Única Activity: solo monta el tema (Material You) y el grafo de navegación. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display
        enableEdgeToEdge()

        setContent {
            EcoTripTheme {
                // Scaffold handles the system bars padding automatically
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EcoTripNavHost(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
