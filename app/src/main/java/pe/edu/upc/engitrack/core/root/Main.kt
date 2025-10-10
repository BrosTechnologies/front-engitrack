package pe.edu.upc.engitrack.core.root

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
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
import androidx.hilt.navigation.compose.hiltViewModel
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.features.dashboard.presentation.DashboardScreen
import pe.edu.upc.engitrack.features.projects.presentation.list.ProjectsScreen
import pe.edu.upc.engitrack.features.profile.presentation.ProfileScreen
import pe.edu.upc.engitrack.features.calendar.presentation.CalendarScreen

@Composable
fun Main(
    authManager: AuthManager,
    onTapProductCard: (Int) -> Unit,
    onNavigateToCreateProject: () -> Unit,
    onNavigateToProjectDetail: (String) -> Unit,
    onNavigateToAuth: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {}
) {

    val navigationItems = listOf(
        NavigationItem(Icons.Default.Home, "Home"),
        NavigationItem(Icons.Default.Work, "Proyectos"),
        NavigationItem(Icons.Default.CalendarToday, "Calendario"),
        NavigationItem(Icons.Default.Person, "Perfil")
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
                            selectedIndex.intValue = index
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
            when (selectedIndex.intValue) {
                0 -> DashboardScreen(
                    authManager = authManager,
                    onNavigateToProjects = { selectedIndex.intValue = 1 }
                )
                1 -> ProjectsScreen(
                    onProjectClick = onNavigateToProjectDetail,
                    onCreateProject = onNavigateToCreateProject
                )
                2 -> CalendarScreen()
                3 -> ProfileScreen(
                    onNavigateToAuth = onNavigateToAuth,
                    onNavigateToEditProfile = onNavigateToEditProfile
                )
            }
        }
    }
}

@Composable
private fun CalendarScreen() {
    pe.edu.upc.engitrack.features.calendar.presentation.CalendarScreen()
}

data class NavigationItem(val icon: ImageVector, val label: String)