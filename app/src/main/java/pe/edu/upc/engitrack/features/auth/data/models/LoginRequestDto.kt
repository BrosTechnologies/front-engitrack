package pe.edu.upc.engitrack.features.auth.data.models

data class LoginRequestDto(
    val email: String,
    val password: String
)