package pe.edu.upc.engitrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import pe.edu.upc.engitrack.core.ui.theme.EasyShopTheme
import pe.edu.upc.engitrack.features.onboarding.presentation.screen.OnboardingScreen
import pe.edu.upc.engitrack.features.welcome.presentation.screen.WelcomeScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasyShopTheme {
                // 1. Creamos un "estado" para recordar qué pantalla estamos mostrando.
                var currentScreen by remember { mutableStateOf("welcome") }

                // 2. Usamos un 'when' para decidir qué Composable mostrar.
                when (currentScreen) {
                    "welcome" -> {
                        // Mostramos la pantalla de bienvenida.
                        // Al hacer clic en "Get Started", cambiamos el estado.
                        WelcomeScreen(
                            onGetStartedClick = {
                                currentScreen = "onboarding"
                            }
                        )
                    }
                    "onboarding" -> {
                        // Cambia al carrusel
                        OnboardingScreen()
                    }
                }
            }
        }
    }
}