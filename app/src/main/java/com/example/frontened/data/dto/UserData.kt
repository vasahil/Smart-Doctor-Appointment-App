package com.example.frontened.data.dto

import android.media.session.MediaSession
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    var name: String,
    var email: String,
    var password: String,
    var mobileNumber: String,
    var role: String,
    val gender: String,
    val dob: String,

    //Doctor Field Only
    val fee: Int? = null,
    val speciality: String? = null,
    val city: String? = null,
    val address: String? = null
)

@Serializable
data class RegisterResponseDto(
    val success: Boolean,
    val data: TokenData?,
    val message: String
)

