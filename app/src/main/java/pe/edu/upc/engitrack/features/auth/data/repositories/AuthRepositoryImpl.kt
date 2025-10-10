package pe.edu.upc.engitrack.features.auth.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.engitrack.core.auth.AuthManager
import pe.edu.upc.engitrack.features.auth.data.models.LoginRequestDto
import pe.edu.upc.engitrack.features.auth.data.models.RegisterRequestDto
import pe.edu.upc.engitrack.features.auth.data.remote.AuthService
import pe.edu.upc.engitrack.features.auth.domain.models.User
import pe.edu.upc.engitrack.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val authManager: AuthManager
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.login(LoginRequestDto(email, password))
                
                if (response.isSuccessful) {
                    response.body()?.let { authResponseDto ->
                        // Log para debugging
                        android.util.Log.d("AuthRepository", "Login response: $authResponseDto")
                        
                        // Verificar que el token no sea null o vacío
                        if (authResponseDto.token.isNullOrBlank()) {
                            return@withContext Result.failure(Exception("Token is null or empty from server"))
                        }
                        
                        // Guardar datos en AuthManager
                        authManager.saveToken(authResponseDto.token!!)
                        authManager.saveUserData(
                            authResponseDto.id,
                            authResponseDto.email,
                            authResponseDto.role,
                            authResponseDto.fullName
                        )
                        
                        val user = User(
                            id = authResponseDto.id,
                            email = authResponseDto.email,
                            role = authResponseDto.role,
                            token = authResponseDto.token!!
                        )
                        return@withContext Result.success(user)
                    } ?: run {
                        return@withContext Result.failure(Exception("Response body is null"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AuthRepository", "Login failed: ${response.code()}, Error: $errorBody")
                    return@withContext Result.failure(Exception("Login failed: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthRepository", "Login exception: ${e.message}", e)
                return@withContext Result.failure(e)
            }
        }
    
    override suspend fun register(
        email: String,
        fullName: String,
        phone: String,
        role: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = service.register(
                RegisterRequestDto(
                    email = email,
                    fullName = fullName,
                    phone = phone,
                    role = role,
                    password = password
                )
            )
            
            if (response.isSuccessful) {
                response.body()?.let { authResponseDto ->
                    // Log para debugging
                    android.util.Log.d("AuthRepository", "Register response: $authResponseDto")
                    
                    // Verificar que el token no sea null o vacío
                    if (authResponseDto.token.isNullOrBlank()) {
                        return@withContext Result.failure(Exception("Token is null or empty from server"))
                    }
                    
                    // Guardar datos en AuthManager
                    authManager.saveToken(authResponseDto.token!!)
                    authManager.saveUserData(
                        authResponseDto.id,
                        authResponseDto.email,
                        authResponseDto.role,
                        authResponseDto.fullName
                    )
                    
                    val user = User(
                        id = authResponseDto.id,
                        email = authResponseDto.email,
                        role = authResponseDto.role,
                        token = authResponseDto.token!!
                    )
                    return@withContext Result.success(user)
                } ?: run {
                    return@withContext Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AuthRepository", "Register failed: ${response.code()}, Error: $errorBody")
                return@withContext Result.failure(Exception("Registration failed: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}