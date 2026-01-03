package com.example.frontened.domain.repo

import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.DoctorDto
import com.example.frontened.data.dto.LoginRequestData
import com.example.frontened.data.dto.ProfileDto
import com.example.frontened.data.dto.RegisterRequestDto
import com.example.frontened.data.dto.RegisterResponseDto
import com.example.frontened.data.dto.SlotDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun registerUser(request: RegisterRequestDto): Flow<ResultState<String>>

    fun loginUser(request: LoginRequestData): Flow<ResultState<String>>

    fun fetchProfile(): Flow<ResultState<ProfileDto>>

    fun fetchNearbyDoctors(
        lat: Double,
        lng: Double,
        distance: Int
    ): Flow<ResultState<List<DoctorDto>>>

    suspend fun addAvailability(
        date: String,
        slots: List<SlotDto>
    ): ResultState<String>

    suspend fun getAvailability(
        doctorId: String,
        date: String
    ): Flow<ResultState<List<SlotDto>>>
}