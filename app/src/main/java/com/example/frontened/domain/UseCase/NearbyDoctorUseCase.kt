package com.example.frontened.domain.UseCase

import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.DoctorDto
import com.example.frontened.domain.repo.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NearbyDoctorUseCase @Inject constructor(
    private val repo: AuthRepository
) {

    suspend operator fun invoke(lat: Double, lng: Double, distance: Int) : Flow<ResultState<List<DoctorDto>>> {
        return repo.fetchNearbyDoctors(lat, lng, distance)
    }
}