package com.example.frontened.presentation.DoctorDashboard

import com.example.frontened.data.dto.SlotDto

data class DoctorDashboardState(
    val loading: Boolean = false,
    val loadingSlots: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val slotsError: String? = null,
    val bookedSlots: List<SlotDto> = emptyList()
)