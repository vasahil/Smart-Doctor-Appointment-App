package com.example.frontened.presentation.loginScreen

data class LoginState(
    val loading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
