package pe.edu.upc.engitrack.core.networking

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class DetailedLoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        Log.d("HTTP_REQUEST", "Sending request ${request.url} with headers ${request.headers}")
        
        val response = chain.proceed(request)
        
        val responseBody = response.peekBody(1024 * 1024) // 1MB limit
        val responseBodyString = responseBody.string()
        
        Log.d("HTTP_RESPONSE", "Received response for ${response.request.url}")
        Log.d("HTTP_RESPONSE", "Response code: ${response.code}")
        Log.d("HTTP_RESPONSE", "Response headers: ${response.headers}")
        Log.d("HTTP_RESPONSE", "Response body: $responseBodyString")
        
        return response
    }
}