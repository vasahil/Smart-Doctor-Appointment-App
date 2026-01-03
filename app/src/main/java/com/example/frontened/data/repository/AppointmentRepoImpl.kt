package com.example.frontened.data.repository


import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.AppointmentDto
import com.example.frontened.data.dto.BookAppointmentRequest
import com.example.frontened.data.dto.DoctorAppointmentDto
import com.example.frontened.domain.di.AppointmentApi
import com.example.frontened.domain.di.AuthApi
import com.example.frontened.domain.repo.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AppointmentRepoImpl @Inject constructor(
    private val api: AppointmentApi
) : AppointmentRepository {

    override fun bookAppointment(
        doctorId: String,
        date: String,
        startTime: String,
        endTime: String
    ): Flow<ResultState<String>> = flow {

        emit(ResultState.Loading)

        try {
            val response = api.bookAppointment(
                BookAppointmentRequest(
                    doctorId = doctorId,
                    date = date,
                    startTime = startTime,
                    endTime = endTime
                )
            )

            if (response.success) {
                emit(ResultState.Success(response.message))
            } else {
                emit(ResultState.Error(response.message))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage ?: "Booking failed"))
        }
    }

    override fun getMyAppointments(): Flow<ResultState<List<AppointmentDto>>> = flow {
        emit(ResultState.Loading)

        try {
            val response = api.getMyAppointments()
            if (response.success && response.data != null) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error(response.message))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage ?: "Failed to load appointments"))
        }
    }

    override fun getDoctorAppointments(): Flow<ResultState<List<DoctorAppointmentDto>>> = flow {
        emit(ResultState.Loading)

        try {
            val response = api.getDoctorAppointments()
            emit(ResultState.Success(response.data))
        } catch (e: HttpException) {
            emit(ResultState.Error("Server error ${e.code()}"))
        } catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage ?: "Something went wrong"))
        }
    }

    override fun cancelAppointment(id: String) = flow {
        emit(ResultState.Loading)
        try {
            api.cancelAppointment(id)
            emit(ResultState.Success("Appointment cancelled"))
        } catch (e: Exception) {
            emit(ResultState.Error("Failed to cancel appointment"))
        }
    }
}
