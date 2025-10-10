package pe.edu.upc.engitrack.features.auth.domain.models

data class User(
    val id: String,
    val email: String,
    val role: String, // "SUPERVISOR", "CONTRACTOR", "USERS"
    val token: String
)
