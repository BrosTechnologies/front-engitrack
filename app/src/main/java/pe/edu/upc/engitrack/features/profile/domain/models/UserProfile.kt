package pe.edu.upc.engitrack.features.profile.domain.models

data class UserProfile(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val role: String
)

data class UpdateProfileRequest(
    val fullName: String,
    val phone: String
)