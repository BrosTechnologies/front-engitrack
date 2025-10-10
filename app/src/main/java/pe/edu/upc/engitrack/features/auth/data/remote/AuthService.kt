package pe.edu.upc.engitrack.features.auth.data.remote

import pe.edu.upc.engitrack.features.auth.data.models.LoginRequestDto
import pe.edu.upc.engitrack.features.auth.data.models.AuthResponseDto
import pe.edu.upc.engitrack.features.auth.data.models.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<AuthResponseDto>

    @Headers("Content-Type: application/json")
    @POST("auth/register")
    suspend fun register(@Body registerRequestDto: RegisterRequestDto): Response<AuthResponseDto>
}