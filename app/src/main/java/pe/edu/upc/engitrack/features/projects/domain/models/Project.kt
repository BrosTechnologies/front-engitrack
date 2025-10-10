package pe.edu.upc.engitrack.features.projects.domain.models

data class Project(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val budget: Double,
    val status: String,
    val ownerUserId: String,
    val tasks: List<Task>
)

data class Task(
    val taskId: String,
    val projectId: String,
    val title: String,
    val status: String,
    val dueDate: String
)

data class CreateProjectRequest(
    val name: String,
    val startDate: String,
    val endDate: String,
    val budget: Double,
    val ownerUserId: String,
    val tasks: List<CreateTaskRequest>
)

data class CreateTaskRequest(
    val title: String,
    val dueDate: String
)

data class UpdateProjectRequest(
    val name: String,
    val budget: Double,
    val endDate: String
)

data class UpdateTaskStatusRequest(
    val status: String
)

// Enums for status
enum class ProjectStatus {
    ACTIVE,
    COMPLETED,
    PENDING,
    CANCELLED
}

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    DONE
}