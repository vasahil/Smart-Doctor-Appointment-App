package com.example.frontened.utils

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
){

    fun saveAccessToken(token: String){
        sharedPreferences.edit {
            putString("ACCESS_TOKEN", token)
        }
    }

    fun getAccessToken(): String? =
         sharedPreferences.getString("ACCESS_TOKEN", null)

    fun getUserRole(): String? {
        val token = getAccessToken() ?: return null
        return JwtUtils.getRole(token)
    }

    fun isTokenExpired(): Boolean {
        val token = getAccessToken() ?: return true
        return JwtUtils.isTokenExpired(token)
    }

    fun clearToken(){
        sharedPreferences.edit { clear() }
    }
}