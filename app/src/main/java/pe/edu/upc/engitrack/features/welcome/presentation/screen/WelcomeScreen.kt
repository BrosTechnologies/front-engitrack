// Ruta: features/welcome/presentation/screen/WelcomeScreen.kt
package pe.edu.upc.engitrack.features.welcome.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.engitrack.R
import pe.edu.upc.engitrack.core.ui.theme.EasyShopTheme

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp), // Padding horizontal
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer para dar un margen superior al contenido
            Spacer(modifier = Modifier.height(100.dp))

            // Bloque del contenido principal (Imagen, Título, Subtítulo)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_welcome),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.9f) // Aumentamos un poco el tamaño
                )

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = stringResource(id = R.string.welcome_title),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.welcome_subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

            // Este Spacer ocupa todo el espacio disponible, empujando el botón hacia abajo
            Spacer(modifier = Modifier.weight(1f))

            // Botón de "Get Started" en la parte inferior
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp) //
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.get_started),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeScreenPreview() {
    EasyShopTheme {
        WelcomeScreen(onGetStartedClick = {})
    }
}