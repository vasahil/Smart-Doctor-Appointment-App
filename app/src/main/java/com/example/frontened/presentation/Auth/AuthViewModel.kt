package com.example.frontened.presentation.Auth

import androidx.lifecycle.ViewModel
import com.example.frontened.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(
        if (isUserLoggedIn()) AuthState.Authenticated
        else AuthState.Unauthenticated
    )

    val authState: StateFlow<AuthState> = _authState

    private fun isUserLoggedIn(): Boolean {
        val token = tokenManager.getAccessToken()
        return token != null && !tokenManager.isTokenExpired()
    }

    fun onLoginSuccess(token: String) {
        tokenManager.saveAccessToken(token)
        _authState.value = AuthState.Authenticated
    }

    fun logout() {
        tokenManager.clearToken()
        _authState.value = AuthState.Unauthenticated
    }
}
