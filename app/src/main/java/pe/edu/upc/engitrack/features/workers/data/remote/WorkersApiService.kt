package pe.edu.upc.engitrack.features.workers.data.remote

import pe.edu.upc.engitrack.features.workers.data.remote.models.*
import retrofit2.Response
import retrofit2.http.*

interface WorkersApiService {

    @GET("/api/workers")
    suspend fun getWorkers(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): Response<List<WorkerDto>>

    @POST("/api/workers")
    suspend fun createWorker(
        @Body request: CreateWorkerRequestDto
    ): Response<WorkerDto>

    @GET("/api/workers/{id}")
    suspend fun getWorkerById(
        @Path("id") id: String
    ): Response<WorkerDto>

    @PUT("/api/workers/{id}")
    suspend fun updateWorker(
        @Path("id") id: String,
        @Body request: UpdateWorkerRequestDto
    ): Response<WorkerDto>

    @DELETE("/api/workers/{id}")
    suspend fun deleteWorker(
        @Path("id") id: String
    ): Response<Unit>

    @GET("/api/projects/{projectId}/workers")
    suspend fun getProjectWorkers(
        @Path("projectId") projectId: String
    ): Response<List<ProjectWorkerDto>>

    @POST("/api/projects/{projectId}/workers")
    suspend fun assignWorkerToProject(
        @Path("projectId") projectId: String,
        @Body request: AssignWorkerToProjectRequestDto
    ): Response<ProjectWorkerDto>

    @DELETE("/api/projects/{projectId}/workers/{workerId}")
    suspend fun removeWorkerFromProject(
        @Path("projectId") projectId: String,
        @Path("workerId") workerId: String
    ): Response<Unit>

    @GET("/api/workers/{id}/assignments")
    suspend fun getWorkerAssignments(
        @Path("id") workerId: String
    ): Response<List<WorkerAssignmentDto>>
}
