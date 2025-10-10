package pe.edu.upc.engitrack.features.profile.data.remote

import pe.edu.upc.engitrack.features.profile.domain.models.UpdateProfileRequest
import pe.edu.upc.engitrack.features.profile.domain.models.UserProfile
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {
    
    @GET("/api/users/profile")
    suspend fun getUserProfile(): Response<UserProfile>
    
    @PATCH("/api/users/profile")
    suspend fun updateUserProfile(
        @Body updateRequest: UpdateProfileRequest
    ): Response<UserProfile>
    
    @GET("/api/users/{id}")
    suspend fun getUserById(
        @Path("id") id: String
    ): Response<UserProfile>
    
    @GET("/api/users")
    suspend fun getAllUsers(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): Response<List<UserProfile>>
}