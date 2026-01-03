package com.example.frontened.presentation.SignupScreen

data class SingUpState(
    val loading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)
