package pe.edu.upc.engitrack.features.auth.domain.repositories

import pe.edu.upc.engitrack.features.auth.domain.models.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(
        email: String, 
        fullName: String, 
        phone: String, 
        role: String, 
        password: String
    ): Result<User>
}