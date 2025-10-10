package pe.edu.upc.engitrack.core.networking

import okhttp3.Interceptor
import okhttp3.Response
import pe.edu.upc.engitrack.core.auth.AuthManager
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authManager: AuthManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val token = authManager.getBearerToken()
        
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}