package com.example.frontened.domain.UseCase

import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.SlotDto
import com.example.frontened.domain.repo.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailabilityUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(doctorId: String, date: String): Flow<ResultState<List<SlotDto>>> {
        return repository.getAvailability(doctorId, date)
    }
}