package com.example.frontened.utils


import android.util.Base64
import org.json.JSONObject
import java.util.Date

object JwtUtils {

    fun getRole(token: String): String? {
        return try {
            val payload = token.split(".")[1]
            val decoded =
                String(Base64.decode(payload, Base64.URL_SAFE))
            JSONObject(decoded).getString("role")
        } catch (e: Exception) {
            null
        }
    }

    fun getUserId(token: String): String? {
        return try {
            val payload = token.split(".")[1]
            val decoded = String(
                android.util.Base64.decode(payload, android.util.Base64.URL_SAFE)
            )
            JSONObject(decoded).getString("userId")
        } catch (e: Exception) {
            null
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val payload = token.split(".")[1]
            val decodeBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodePayload = String(decodeBytes)

            val jsonObject = JSONObject(decodePayload)
            val exp = jsonObject.getLong("exp")

            val expiryDate = Date(exp * 1000)
            expiryDate.before(Date())
        }catch (e: Exception) {
            true
        }
    }
}