package com.example.frontened.presentation.Auth

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}