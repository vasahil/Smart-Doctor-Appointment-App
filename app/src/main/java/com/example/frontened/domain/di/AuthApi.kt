package com.example.frontened.domain.di

import com.example.frontened.data.dto.ApiResponse
import com.example.frontened.data.dto.AppointmentDto
import com.example.frontened.data.dto.AvailabilityRequest
import com.example.frontened.data.dto.BookAppointmentRequest
import com.example.frontened.data.dto.DoctorAvailabilityDto
import com.example.frontened.data.dto.LoginRequestData
import com.example.frontened.data.dto.LoginResponseDto
import com.example.frontened.data.dto.NearbyDoctorResponse
import com.example.frontened.data.dto.ProfileResponseDto
import com.example.frontened.data.dto.RegisterRequestDto
import com.example.frontened.data.dto.RegisterResponseDto
import com.example.frontened.data.dto.SlotDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("api/auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequestDto
    ): RegisterResponseDto

    @POST("api/auth/login")
    suspend fun loginUser(
        @Body requestData: LoginRequestData
    ): LoginResponseDto

    @POST("api/auth/refresh")
    suspend fun refreshToken(): LoginResponseDto

    @GET("api/profile")
    suspend fun fetchProfile() : ProfileResponseDto


    @GET("api/doctors/nearby")
    suspend fun getNearbyDoctors(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("distance") distance: Int

    ): NearbyDoctorResponse


    @POST("api/availability/add")
    suspend fun addAvailability(
        @Body request: AvailabilityRequest
    ): ApiResponse<DoctorAvailabilityDto>


    @GET("api/availability")
    suspend fun getDoctorAvailability(
        @Query("doctorId") doctorId: String,
        @Query("date") date: String
    ): ApiResponse<DoctorAvailabilityDto>


}