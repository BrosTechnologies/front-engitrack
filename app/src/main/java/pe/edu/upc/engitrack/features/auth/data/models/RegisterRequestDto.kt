package pe.edu.upc.engitrack.features.auth.data.models

data class RegisterRequestDto(
    val email: String,
    val fullName: String,
    val phone: String,
    val role: String, // "SUPERVISOR", "CONTRACTOR", "WORKER"
    val password: String
)