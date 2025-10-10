package pe.edu.upc.engitrack.features.profile.domain.model

data class UserProfile(
    val id: Int,
    val email: String,
    val fullName: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val avatar: String? = null,
    val createdAt: String? = null
)