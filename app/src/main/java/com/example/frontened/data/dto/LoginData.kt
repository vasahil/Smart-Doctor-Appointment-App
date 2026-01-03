package com.example.frontened.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestData(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val success: Boolean,
    val data: TokenData?
)

@Serializable
data class TokenData(
    val accessToken: String
)
