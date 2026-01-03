package com.example.frontened.presentation.DoctorAppointments

import com.example.frontened.data.dto.DoctorAppointmentDto

data class DoctorAppointmentsState(
    val loading: Boolean = false,
    val appointments: List<DoctorAppointmentDto> = emptyList(),
    val error: String? = null,
    val selectedFilter: AppointmentFilter = AppointmentFilter.ALL
)
enum class AppointmentFilter {
    ALL, PENDING, CONFIRMED, COMPLETED, CANCELLED
}