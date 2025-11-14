package pe.edu.upc.engitrack.features.workers.data.remote.models

data class WorkerAssignmentDto(
    val assignmentId: String,
    val projectId: String,
    val projectName: String,
    val startDate: String,
    val endDate: String
)
