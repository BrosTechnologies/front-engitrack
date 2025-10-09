package pe.edu.upc.engitrack.features.auth.data.remote

import pe.edu.upc.engitrack.features.auth.data.models.LoginRequestDto
import pe.edu.upc.engitrack.features.auth.data.models.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): Response<LoginResponseDto>
}