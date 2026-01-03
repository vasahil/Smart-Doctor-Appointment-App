package com.example.frontened.presentation.ProfileScreen

import com.example.frontened.data.dto.ProfileDto

data class ProfileState(
    val loading: Boolean = false,
    val data: ProfileDto? = null,
    val error: String? = null
)
