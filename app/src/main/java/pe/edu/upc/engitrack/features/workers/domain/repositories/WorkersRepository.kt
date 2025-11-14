package pe.edu.upc.engitrack.features.workers.domain.repositories

import pe.edu.upc.engitrack.features.workers.domain.models.*

interface WorkersRepository {
    suspend fun getWorkers(page: Int?, pageSize: Int?): Result<List<Worker>>
    suspend fun createWorker(
        fullName: String,
        documentNumber: String,
        phone: String,
        position: String,
        hourlyRate: Double,
        projectId: String?
    ): Result<Worker>
    suspend fun getWorkerById(id: String): Result<Worker>
    suspend fun updateWorker(
        id: String,
        fullName: String,
        phone: String,
        position: String,
        hourlyRate: Double
    ): Result<Worker>
    suspend fun deleteWorker(id: String): Result<Unit>
    suspend fun getProjectWorkers(projectId: String): Result<List<ProjectWorker>>
    suspend fun assignWorkerToProject(
        projectId: String,
        workerId: String,
        startDate: String,
        endDate: String
    ): Result<ProjectWorker>
    suspend fun removeWorkerFromProject(projectId: String, workerId: String): Result<Unit>
    suspend fun getWorkerAssignments(workerId: String): Result<List<WorkerAssignment>>
}
