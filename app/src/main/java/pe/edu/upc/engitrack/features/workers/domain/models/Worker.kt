package pe.edu.upc.engitrack.features.workers.domain.models

data class Worker(
    val id: String,
    val fullName: String,
    val documentNumber: String,
    val phone: String,
    val position: String,
    val hourlyRate: Double,
    val assignments: List<WorkerAssignmentSimple>
)

data class WorkerAssignmentSimple(
    val id: String,
    val workerId: String,
    val projectId: String,
    val startDate: String,
    val endDate: String
)

data class ProjectWorker(
    val workerId: String,
    val fullName: String,
    val documentNumber: String,
    val phone: String,
    val position: String,
    val hourlyRate: Double,
    val assignmentId: String,
    val projectId: String,
    val startDate: String,
    val endDate: String
)

data class WorkerAssignment(
    val assignmentId: String,
    val projectId: String,
    val projectName: String,
    val startDate: String,
    val endDate: String
)
