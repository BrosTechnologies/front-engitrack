package pe.edu.upc.engitrack.core.root

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import pe.edu.upc.engitrack.features.home.presentation.home.Home

@Composable
fun Main(onTapProductCard: (Int) -> Unit, onNavigateToProfile: () -> Unit) {

    val navigationItems = listOf(
        NavigationItem(Icons.Default.Home, "Home"),
        NavigationItem(Icons.Default.Favorite, "Favorite"),
        NavigationItem(Icons.Default.ShoppingCart, "Cart"),
        NavigationItem(Icons.Default.Person, "Profile")
    )

    val selectedIndex = remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == selectedIndex.intValue,
                        onClick = {
                            if (item.label == "Profile") {
                                onNavigateToProfile()
                            } else {
                                selectedIndex.intValue = index
                            }
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }

            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Home(
                onTapProductCard = { id ->
                    onTapProductCard(id)
                },
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

data class NavigationItem(val icon: ImageVector, val label: String)