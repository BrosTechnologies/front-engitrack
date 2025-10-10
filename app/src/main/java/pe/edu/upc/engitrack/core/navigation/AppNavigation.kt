package pe.edu.upc.engitrack.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.core.root.Main
import pe.edu.upc.engitrack.core.ui.theme.EasyShopTheme
import pe.edu.upc.engitrack.features.auth.presentation.login.LoginScreen
import pe.edu.upc.engitrack.features.auth.presentation.register.RegisterScreen
import pe.edu.upc.engitrack.features.home.presentation.productdetail.ProductDetail
import pe.edu.upc.engitrack.features.home.presentation.productdetail.ProductDetailViewModel
import pe.edu.upc.engitrack.features.projects.presentation.create.CreateProjectScreen
import pe.edu.upc.engitrack.features.projects.presentation.detail.ProjectDetailScreen

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthManagerEntryPoint {
    fun authManager(): AuthManager
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, 
        startDestination = Route.Login.route
    ) {
        composable(Route.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Route.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Route.Main.route) {
                        // Limpiar el stack de navegación para que no pueda volver atrás
                        popUpTo(Route.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Route.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Register.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Route.Main.route) {
                        // Limpiar el stack de navegación para que no pueda volver atrás
                        popUpTo(Route.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Route.Main.route) {
            val context = LocalContext.current
            val authManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    AuthManagerEntryPoint::class.java
                ).authManager()
            }
            
            Main(
                authManager = authManager,
                onTapProductCard = { productId ->
                    navController.navigate("${Route.ProductDetail.route}/$productId")
                },
                onNavigateToCreateProject = {
                    navController.navigate(Route.CreateProject.route)
                },
                onNavigateToProjectDetail = { projectId ->
                    navController.navigate("${Route.ProjectDetail.route}/$projectId")
                }
            )
        }

        composable(Route.CreateProject.route) {
            CreateProjectScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Route.ProductDetail.routeWithArgument,
            arguments = listOf(navArgument(Route.ProductDetail.argument) {
                type = NavType.IntType
            })
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.let { arguments ->
                val productId = arguments.getInt(Route.ProductDetail.argument)
                val productDetailViewModel: ProductDetailViewModel = hiltViewModel()

                productDetailViewModel.getProductById(productId)
                ProductDetail(productDetailViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    EasyShopTheme {
        AppNavigation()
    }
}