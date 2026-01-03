package com.example.frontened.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class AvailabilityRequest(
    val date: String,
    val slots: List<SlotDto>
)

@Serializable
data class DoctorAvailabilityDto(
    val doctorId: String,
    val date: String,
    val slots: List<SlotDto>
)

@Serializable
data class SlotDto(
    val startTime: String,
    val endTime: String,
    val isBooked: Boolean = false
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String
)