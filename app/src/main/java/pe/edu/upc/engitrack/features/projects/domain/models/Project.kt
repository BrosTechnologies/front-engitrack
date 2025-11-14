package pe.edu.upc.engitrack.features.projects.domain.models

import androidx.compose.ui.graphics.Color

data class Project(
    val id: String,
    val name: String,
    val startDate: String,
    val endDate: String,
    val budget: Double,
    val status: String,
    val priority: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
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
    val priority: Int = 1, // 0=LOW, 1=MEDIUM, 2=HIGH
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
    val endDate: String,
    val priority: Int // 0=LOW, 1=MEDIUM, 2=HIGH
)

data class UpdatePriorityRequest(
    val priority: Int // 0=LOW, 1=MEDIUM, 2=HIGH
)

data class UpdatePriorityStringRequest(
    val priority: String // "LOW", "MEDIUM", "HIGH"
)

data class UpdateTaskStatusRequest(
    val status: String
)

// Priority enum con colores mejorados
enum class Priority(val value: Int, val displayName: String, val color: Color) {
    LOW(0, "Baja", Color(0xFF4CAF50)),      // Verde
    MEDIUM(1, "Media", Color(0xFFFF9800)),   // Naranja  
    HIGH(2, "Alta", Color(0xFFE53E3E));      // Rojo
    
    companion object {
        fun fromInt(value: Int) = values().find { it.value == value } ?: MEDIUM
        fun fromString(value: String) = when(value.uppercase()) {
            "LOW", "BAJA" -> LOW
            "MEDIUM", "MEDIA" -> MEDIUM  
            "HIGH", "ALTA" -> HIGH
            else -> MEDIUM
        }
    }
}

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