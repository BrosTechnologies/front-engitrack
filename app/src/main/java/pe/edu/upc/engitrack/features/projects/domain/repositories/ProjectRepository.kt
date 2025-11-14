package pe.edu.upc.engitrack.features.projects.domain.repositories

import pe.edu.upc.engitrack.features.projects.domain.models.*

interface ProjectRepository {
    suspend fun getProjects(status: String? = null, query: String? = null): Result<List<Project>>
    suspend fun getProjectById(id: String): Result<Project>
    suspend fun createProject(project: CreateProjectRequest): Result<Project>
    suspend fun updateProject(id: String, updateRequest: UpdateProjectRequest): Result<Project>
    suspend fun createTask(projectId: String, task: CreateTaskRequest): Result<Task>
    suspend fun updateTaskStatus(projectId: String, taskId: String, status: String): Result<Task>
    suspend fun deleteTask(projectId: String, taskId: String): Result<Unit>
    suspend fun deleteProject(id: String): Result<Unit>
    suspend fun completeProject(id: String): Result<Project>
}