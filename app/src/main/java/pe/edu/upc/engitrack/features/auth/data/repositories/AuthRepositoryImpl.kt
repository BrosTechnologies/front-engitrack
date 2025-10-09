package pe.edu.upc.engitrack.features.auth.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.engitrack.features.auth.data.models.LoginRequestDto
import pe.edu.upc.engitrack.features.auth.data.remote.AuthService
import pe.edu.upc.engitrack.features.auth.domain.models.User
import pe.edu.upc.engitrack.features.auth.domain.repositories.AuthRepository

class AuthRepositoryImpl(private val service: AuthService) : AuthRepository {
    override suspend fun login(username: String, password: String): User? =
        withContext(Dispatchers.IO) {

            val response = service.login(LoginRequestDto(username, password))

            if (response.isSuccessful) {
                response.body()?.let { loginResponseDto ->
                    return@withContext User(
                        name = "${loginResponseDto.lastName}, ${loginResponseDto.firstName}",
                        image = loginResponseDto.image,
                        username = loginResponseDto.username
                    )
                }
            }

            return@withContext null
        }
}