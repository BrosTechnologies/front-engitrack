package pe.edu.upc.engitrack.core.navigation

sealed class Route(val route: String){
    object Login: Route("login")
    object Register: Route("register")
    object Main: Route("main")
    object CreateProject: Route("create_project")
    object EditProfile: Route("edit_profile")
    object WorkerForm: Route("worker_form")
    object WorkerAssignments: Route("worker_assignments")
    object ProjectDetail: Route("project_detail") {
        const val routeWithArgument = "project_detail/{id}"
        const val argument = "id"
    }
    object WorkersSelector: Route("workers_selector") {
        const val routeWithArgument = "workers_selector/{projectId}/{projectEndDate}"
        const val argumentProjectId = "projectId"
        const val argumentProjectEndDate = "projectEndDate"
    }
    object ProductDetail: Route("product_detail") {
        const val routeWithArgument = "product_detail/{id}"
        const val argument = "id"
    }
}