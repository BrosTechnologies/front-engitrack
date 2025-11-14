package pe.edu.upc.engitrack.features.workers.data.remote.models

data class WorkerDto(
    val id: String,
    val fullName: String,
    val documentNumber: String,
    val phone: String,
    val position: String,
    val hourlyRate: Double,
    val assignments: List<WorkerAssignmentSimpleDto>
)

data class WorkerAssignmentSimpleDto(
    val id: String,
    val workerId: String,
    val projectId: String,
    val startDate: String,
    val endDate: String
)

data class CreateWorkerRequestDto(
    val fullName: String,
    val documentNumber: String,
    val phone: String,
    val position: String,
    val hourlyRate: Double,
    val projectId: String? = null
)

data class UpdateWorkerRequestDto(
    val fullName: String,
    val phone: String,
    val position: String,
    val hourlyRate: Double
)
