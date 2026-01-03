package com.example.frontened.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponseDto(
    val success: Boolean,
    val data: ProfileDto
)

@Serializable
data class ProfileDto(
    val image: String?,
    val name: String,
    val email: String,
    val mobileNumber:String,
    val role: String,
    val dob: String,

    // Doctor-only (nullable for patient)
    val speciality: String? = null,
    val fee: Int? = null,
    val location: LocationDto? = null

)






