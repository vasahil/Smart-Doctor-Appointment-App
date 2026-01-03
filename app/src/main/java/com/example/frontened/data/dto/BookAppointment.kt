package com.example.frontened.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class BookAppointmentRequest(
    val doctorId: String,
    val date: String,
    val startTime: String,
    val endTime: String
)

@Serializable
data class AppointmentDto(
    val _id: String,
    val doctorId: String,
    val patientId: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

