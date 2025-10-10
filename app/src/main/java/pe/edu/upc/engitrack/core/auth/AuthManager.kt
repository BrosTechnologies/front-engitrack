package pe.edu.upc.engitrack.core.auth

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_FULL_NAME = "user_full_name"
    }
    
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_JWT_TOKEN, token).apply()
    }
    
    fun getToken(): String? = prefs.getString(KEY_JWT_TOKEN, null)
    
    fun getBearerToken(): String? {
        val token = getToken()
        return if (token != null) "Bearer $token" else null
    }
    
    fun saveUserData(userId: String, email: String, role: String, fullName: String? = null) {
        with(prefs.edit()) {
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            fullName?.let { putString(KEY_USER_FULL_NAME, it) }
            apply()
        }
    }
    
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)
    
    fun getUserFullName(): String? = prefs.getString(KEY_USER_FULL_NAME, null)
    
    fun getUserData(): UserData? {
        val id = getUserId()
        val email = getUserEmail()
        val role = getUserRole()
        val fullName = getUserFullName()
        
        return if (id != null && email != null) {
            UserData(id, email, role, fullName)
        } else null
    }
    
    fun isLoggedIn(): Boolean = getToken() != null
    
    fun logout() {
        prefs.edit().clear().apply()
    }
}

data class UserData(
    val id: String,
    val email: String,
    val role: String?,
    val fullName: String?
)