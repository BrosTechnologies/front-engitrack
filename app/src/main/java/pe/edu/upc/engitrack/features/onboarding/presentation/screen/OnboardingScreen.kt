// Ruta: features/onboarding/presentation/screen/OnboardingScreen.kt
package pe.edu.upc.engitrack.features.onboarding.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.R
import pe.edu.upc.engitrack.core.ui.theme.EasyShopTheme
import pe.edu.upc.engitrack.features.onboarding.domain.model.OnboardingPage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen() {
    val pages = listOf(
        OnboardingPage.First,
        OnboardingPage.Second,
        OnboardingPage.Third
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope() // Necesario para animar el scroll del pager

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState
            ) { pageIndex ->
                OnboardingPageItem(
                    modifier = Modifier.fillMaxSize(),
                    page = pages[pageIndex]
                )
            }

            // Fila inferior que contiene los botones y el indicador
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                // Indicador de página en el centro
                PagerIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    pagerState = pagerState
                )

                // Botón de "Skip" a la izquierda
                TextButton(
                    onClick = {
                        // Aquí irá la lógica para saltar el onboarding
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(text = stringResource(id = R.string.onboarding_skip))
                }

                // Botón de "Next" / "Get Started" a la derecha
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            // Si no es la última página, avanza a la siguiente
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            // Aquí irá la lógica para ir al login
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6)
                    )
                ) {
                    // El texto del botón cambia si estamos en la última página
                    val buttonText = if (pagerState.currentPage == pages.size - 1) {
                        stringResource(id = R.string.onboarding_get_started)
                    } else {
                        stringResource(id = R.string.onboarding_next)
                    }
                    Text(text = buttonText)
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pagerState.pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            val color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun OnboardingPageItem(
    modifier: Modifier = Modifier,
    page: OnboardingPage
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(0.9f),
            painter = painterResource(id = page.image),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = stringResource(id = page.title),
            style = MaterialTheme. typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = page.description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingScreenPreview() {
    EasyShopTheme {
        OnboardingScreen()
    }
}