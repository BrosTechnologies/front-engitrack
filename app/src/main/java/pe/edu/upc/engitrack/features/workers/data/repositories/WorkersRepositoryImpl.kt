package pe.edu.upc.engitrack.features.workers.data.repositories

import pe.edu.upc.engitrack.features.workers.data.remote.WorkersApiService
import pe.edu.upc.engitrack.features.workers.data.remote.models.*
import pe.edu.upc.engitrack.features.workers.domain.models.*
import pe.edu.upc.engitrack.features.workers.domain.repositories.WorkersRepository
import java.time.LocalDate
import javax.inject.Inject

class WorkersRepositoryImpl @Inject constructor(
    private val apiService: WorkersApiService
) : WorkersRepository {

    override suspend fun getWorkers(page: Int?, pageSize: Int?): Result<List<Worker>> {
        return try {
            val response = apiService.getWorkers(page, pageSize)
            if (response.isSuccessful && response.body() != null) {
                val workers = response.body()!!.map { it.toDomain() }
                Result.success(workers)
            } else {
                Result.failure(Exception("Error al obtener workers: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createWorker(
        fullName: String,
        documentNumber: String,
        phone: String,
        position: String,
        hourlyRate: Double,
        projectId: String?
    ): Result<Worker> {
        return try {
            val request = CreateWorkerRequestDto(
                fullName = fullName,
                documentNumber = documentNumber,
                phone = phone,
                position = position,
                hourlyRate = hourlyRate,
                projectId = projectId
            )
            val response = apiService.createWorker(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al crear worker: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWorkerById(id: String): Result<Worker> {
        return try {
            val response = apiService.getWorkerById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al obtener worker: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateWorker(
        id: String,
        fullName: String,
        phone: String,
        position: String,
        hourlyRate: Double
    ): Result<Worker> {
        return try {
            val request = UpdateWorkerRequestDto(
                fullName = fullName,
                phone = phone,
                position = position,
                hourlyRate = hourlyRate
            )
            val response = apiService.updateWorker(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al actualizar worker: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWorker(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteWorker(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar worker: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProjectWorkers(projectId: String): Result<List<ProjectWorker>> {
        return try {
            val response = apiService.getProjectWorkers(projectId)
            if (response.isSuccessful && response.body() != null) {
                val projectWorkers = response.body()!!.map { it.toDomain() }
                Result.success(projectWorkers)
            } else {
                Result.failure(Exception("Error al obtener workers del proyecto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun assignWorkerToProject(
        projectId: String,
        workerId: String,
        startDate: String,
        endDate: String
    ): Result<ProjectWorker> {
        return try {
            val request = AssignWorkerToProjectRequestDto(
                workerId = workerId,
                startDate = startDate,
                endDate = endDate
            )
            val response = apiService.assignWorkerToProject(projectId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error al asignar worker al proyecto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeWorkerFromProject(projectId: String, workerId: String): Result<Unit> {
        return try {
            val response = apiService.removeWorkerFromProject(projectId, workerId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al remover worker del proyecto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWorkerAssignments(workerId: String): Result<List<WorkerAssignment>> {
        return try {
            val response = apiService.getWorkerAssignments(workerId)
            if (response.isSuccessful && response.body() != null) {
                val assignments = response.body()!!.map { it.toDomain() }
                Result.success(assignments)
            } else {
                Result.failure(Exception("Error al obtener asignaciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Mappers
    private fun WorkerDto.toDomain() = Worker(
        id = id,
        fullName = fullName,
        documentNumber = documentNumber,
        phone = phone,
        position = position,
        hourlyRate = hourlyRate,
        assignments = assignments.map { it.toDomain() }
    )
    
    private fun WorkerAssignmentSimpleDto.toDomain() = WorkerAssignmentSimple(
        id = id,
        workerId = workerId,
        projectId = projectId,
        startDate = startDate,
        endDate = endDate
    )

    private fun ProjectWorkerDto.toDomain() = ProjectWorker(
        workerId = workerId,
        fullName = fullName,
        documentNumber = documentNumber,
        phone = phone,
        position = position,
        hourlyRate = hourlyRate,
        assignmentId = assignmentId,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate)
    )

    private fun WorkerAssignmentDto.toDomain() = WorkerAssignment(
        assignmentId = assignmentId,
        projectId = projectId,
        projectName = projectName,
        startDate = startDate,
        endDate = endDate
    )
}
