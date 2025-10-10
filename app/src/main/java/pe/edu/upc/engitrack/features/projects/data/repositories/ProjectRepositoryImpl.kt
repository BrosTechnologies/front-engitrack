package pe.edu.upc.engitrack.features.projects.data.repositories

import pe.edu.upc.engitrack.features.projects.data.remote.ProjectApiService
import pe.edu.upc.engitrack.features.projects.domain.models.*
import pe.edu.upc.engitrack.features.projects.domain.repositories.ProjectRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectApiService: ProjectApiService
) : ProjectRepository {

    override suspend fun getProjects(status: String?, query: String?): Result<List<Project>> {
        return try {
            android.util.Log.d("ProjectRepository", "Getting projects with status: $status, query: $query")
            val response = projectApiService.getProjects(status, query)
            android.util.Log.d("ProjectRepository", "API Response code: ${response.code()}")
            
            if (response.isSuccessful) {
                val projects = response.body() ?: emptyList()
                android.util.Log.d("ProjectRepository", "Successfully got ${projects.size} projects")
                Result.success(projects)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("ProjectRepository", "Error getting projects: ${response.code()}, Error: $errorBody")
                Result.failure(Exception("Error al obtener proyectos: ${response.message()} - $errorBody"))
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectRepository", "Exception getting projects", e)
            Result.failure(e)
        }
    }

    override suspend fun getProjectById(id: String): Result<Project> {
        return try {
            val response = projectApiService.getProjectById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Proyecto no encontrado"))
            } else {
                Result.failure(Exception("Error al obtener proyecto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createProject(project: CreateProjectRequest): Result<Project> {
        return try {
            val response = projectApiService.createProject(project)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al crear proyecto"))
            } else {
                Result.failure(Exception("Error al crear proyecto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProject(id: String, updateRequest: UpdateProjectRequest): Result<Project> {
        return try {
            val response = projectApiService.updateProject(id, updateRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al actualizar proyecto"))
            } else {
                Result.failure(Exception("Error al actualizar proyecto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createTask(projectId: String, task: CreateTaskRequest): Result<Task> {
        return try {
            val response = projectApiService.createTask(projectId, task)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al crear tarea"))
            } else {
                Result.failure(Exception("Error al crear tarea: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTaskStatus(projectId: String, taskId: String, status: String): Result<Task> {
        return try {
            val response = projectApiService.updateTaskStatus(
                projectId, 
                taskId, 
                UpdateTaskStatusRequest(status)
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al actualizar estado de tarea"))
            } else {
                Result.failure(Exception("Error al actualizar estado de tarea: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(projectId: String, taskId: String): Result<Unit> {
        return try {
            val response = projectApiService.deleteTask(projectId, taskId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar tarea: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeProject(id: String): Result<Project> {
        return try {
            val response = projectApiService.completeProject(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Error al completar proyecto"))
            } else {
                Result.failure(Exception("Error al completar proyecto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}