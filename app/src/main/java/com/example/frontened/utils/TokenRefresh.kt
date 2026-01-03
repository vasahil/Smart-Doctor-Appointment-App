package com.example.frontened.utils

import com.example.frontened.domain.di.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import javax.inject.Inject

class TokenRefresh @Inject constructor(
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= 2) return null

        val refreshRequest = Request.Builder()
            .url("https://smart-doctor-backend-8blr.onrender.com/api/auth/refresh")
            .post("".toRequestBody("application/json".toMediaType()))
            .build()

        val client = OkHttpClient()

        val refreshResponse = client.newCall(refreshRequest).execute()

        if (!refreshResponse.isSuccessful) return null

        val body = refreshResponse.body?.string() ?: return null

        val newToken = JSONObject(body).getString("accessToken")

        tokenManager.saveAccessToken(newToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }
}


private fun responseCount(response: Response): Int {
    var count = 1
    var priorResponse = response.priorResponse
    while (priorResponse != null) {
        count++
        priorResponse = priorResponse.priorResponse
    }
    return count
}
