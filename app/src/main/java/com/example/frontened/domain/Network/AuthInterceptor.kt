package com.example.frontened.domain.Network


import androidx.core.view.DragAndDropPermissionsCompat.request
import com.example.frontened.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        // Skip auth endpoints
        if (
            path.contains("/api/auth/login") ||
            path.contains("/api/auth/register") ||
            path.contains("/api/auth/refresh")
        ) {
            return chain.proceed(request)
        }

        val token = tokenManager.getAccessToken()

        val newRequest = request.newBuilder().apply {
            if (token != null && !tokenManager.isTokenExpired()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}
