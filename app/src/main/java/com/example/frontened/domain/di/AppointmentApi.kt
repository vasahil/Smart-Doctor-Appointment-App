package com.example.frontened.domain.di

import com.example.frontened.data.dto.ApiResponse
import com.example.frontened.data.dto.AppointmentDto
import com.example.frontened.data.dto.BookAppointmentRequest
import com.example.frontened.data.dto.DoctorAppointmentResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentApi {

    @POST("api/appointments/book")
    suspend fun bookAppointment(
        @Body request: BookAppointmentRequest
    ): ApiResponse<AppointmentDto>

    @GET("api/appointments/my")
    suspend fun getMyAppointments(): ApiResponse<List<AppointmentDto>>

    @GET("api/appointments/doctor")
    suspend fun getDoctorAppointments(): DoctorAppointmentResponse

    @PATCH("api/appointments/cancel/{id}")
    suspend fun cancelAppointment(
        @Path("id") appointmentId: String
    ): ApiResponse<AppointmentDto>
}