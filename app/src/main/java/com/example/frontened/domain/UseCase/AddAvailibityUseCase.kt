package com.example.frontened.domain.UseCase

import com.example.frontened.data.dto.SlotDto
import com.example.frontened.domain.repo.AuthRepository
import javax.inject.Inject

class AddAvailabilityUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(
        date: String,
        slots: List<SlotDto>
    ) = repo.addAvailability(date, slots)
}