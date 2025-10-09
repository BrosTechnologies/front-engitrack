package pe.edu.upc.engitrack.core.navigation

sealed class Route(val route: String){
    object Login: Route("login")
    object Register: Route("register")
    object Main: Route("main")
    object ProductDetail: Route("product_detail") {
        const val routeWithArgument = "product_detail/{id}"
        const val argument = "id"
    }
}