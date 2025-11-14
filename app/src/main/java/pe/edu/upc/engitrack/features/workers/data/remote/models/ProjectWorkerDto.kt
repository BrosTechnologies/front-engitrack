package pe.edu.upc.engitrack.features.workers.data.remote.models

data class ProjectWorkerDto(
    val workerId: String,
    val fullName: String,
    val documentNumber: String,
    val phone: String,
    val position: String,
    val hourlyRate: Double,
    val assignmentId: String,
    val startDate: String,
    val endDate: String
)

data class AssignWorkerToProjectRequestDto(
    val workerId: String,
    val startDate: String,
    val endDate: String
)
