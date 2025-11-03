package pe.edu.upc.engitrack.features.projects.data.remote

import pe.edu.upc.engitrack.features.projects.domain.models.*
import retrofit2.Response
import retrofit2.http.*

interface ProjectApiService {
    
    @POST("/api/projects")
    suspend fun createProject(
        @Body request: CreateProjectRequest
    ): Response<Project>
    
    @GET("/api/projects")
    suspend fun getProjects(
        @Query("status") status: String? = null,
        @Query("q") q: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<List<Project>>
    
    @GET("/api/projects/{id}")
    suspend fun getProjectById(
        @Path("id") id: String
    ): Response<Project>
    
    @PATCH("/api/projects/{id}")
    suspend fun updateProject(
        @Path("id") id: String,
        @Body updateRequest: UpdateProjectRequest
    ): Response<Project>
    
    @PATCH("/api/projects/{id}/priority")
    suspend fun updatePriority(
        @Path("id") projectId: String,
        @Body request: UpdatePriorityRequest
    ): Response<Project>
    
    @PATCH("/api/projects/{id}/priority/string")
    suspend fun updatePriorityString(
        @Path("id") projectId: String,
        @Body request: UpdatePriorityStringRequest
    ): Response<Project>
    
    @POST("/api/projects/{id}/tasks")
    suspend fun createTask(
        @Path("id") projectId: String,
        @Body task: CreateTaskRequest
    ): Response<Task>
    
    @PATCH("/api/projects/{id}/tasks/{taskId}/status")
    suspend fun updateTaskStatus(
        @Path("id") projectId: String,
        @Path("taskId") taskId: String,
        @Body statusRequest: UpdateTaskStatusRequest
    ): Response<Task>
    
    @DELETE("/api/projects/{id}/tasks/{taskId}")
    suspend fun deleteTask(
        @Path("id") projectId: String,
        @Path("taskId") taskId: String
    ): Response<Unit>
    
    @PATCH("/api/projects/{id}/complete")
    suspend fun completeProject(
        @Path("id") id: String
    ): Response<Project>
}