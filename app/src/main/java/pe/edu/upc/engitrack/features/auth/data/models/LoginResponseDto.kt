package pe.edu.upc.engitrack.features.auth.data.models

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("userId") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String,
    @SerializedName("accessToken") val token: String?
)