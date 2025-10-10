package pe.edu.upc.engitrack.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.edu.upc.engitrack.core.root.Main
import pe.edu.upc.engitrack.core.ui.theme.EasyShopTheme

import pe.edu.upc.engitrack.features.auth.presentation.di.PresentationModule.getLoginViewModel
import pe.edu.upc.engitrack.features.auth.presentation.di.PresentationModule.getEditProfileViewModel
import pe.edu.upc.engitrack.features.auth.presentation.di.PresentationModule.getProfileViewModel
import pe.edu.upc.engitrack.features.auth.presentation.di.PresentationModule.getRegisterViewModel
import pe.edu.upc.engitrack.features.auth.presentation.login.Login
import pe.edu.upc.engitrack.features.auth.presentation.register.Register
import pe.edu.upc.engitrack.features.home.presentation.productdetail.ProductDetail
import pe.edu.upc.engitrack.features.home.presentation.productdetail.ProductDetailViewModel
import pe.edu.upc.engitrack.features.profile.presentation.editprofile.EditProfile
import pe.edu.upc.engitrack.features.profile.presentation.profile.Profile

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel = getLoginViewModel()
    val registerViewModel = getRegisterViewModel()
    val profileViewModel = getProfileViewModel()
    val editProfileViewModel = getEditProfileViewModel()


    NavHost(navController, startDestination = Route.Login.route) {
        composable(Route.Login.route) {
            Login(
                loginViewModel,
                onLogin = {
                    navController.navigate(Route.Main.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register.route)
                }
            )
        }

        composable(Route.Register.route) {
            Register(
                viewModel = registerViewModel,
                onLogin = {
                    navController.navigate(Route.Login.route)
                },
                onRegister = {
                    navController.navigate(Route.Main.route)
                }
            )
        }


        composable(Route.Main.route) {
            Main(
                onTapProductCard = { productId ->
                navController.navigate("${Route.ProductDetail.route}/$productId")
                },
                onNavigateToProfile = {
                    navController.navigate(Route.Profile.route)
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

        composable(Route.Profile.route) {
            Profile(
                onNavigateToEditProfile = {
                    navController.navigate(Route.EditProfile.route)
                },
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Route.EditProfile.route) {
            EditProfile(
                onSave = {
                    navController.popBackStack()
                }
            )
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