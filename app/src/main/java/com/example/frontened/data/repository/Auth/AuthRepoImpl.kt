package com.example.frontened.data.repository.Auth

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.AvailabilityRequest
import com.example.frontened.data.dto.DoctorDto
import com.example.frontened.data.dto.LoginRequestData
import com.example.frontened.data.dto.ProfileDto
import com.example.frontened.data.dto.RegisterRequestDto
import com.example.frontened.data.dto.SlotDto
import com.example.frontened.domain.di.AuthApi
import com.example.frontened.domain.repo.AuthRepository
import com.example.frontened.utils.TokenManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class AuthRepoImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override fun registerUser(
        request: RegisterRequestDto
    ): Flow<ResultState<String>> = flow {

        emit(ResultState.Loading)

        try {
            val response = api.registerUser(request)
            Log.d("AUTH_REPO", "Response: success=${response.success}, message=${response.message}")
            if (response.success && response.data != null) {
                tokenManager.saveAccessToken(response.data.accessToken)
                emit(ResultState.Success(response.message))
            } else {
                emit(ResultState.Error(response.message))
            }

        } catch (e: CancellationException) {
            throw e

        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("AUTH_REPO", "HTTP Error: ${e.code()}, Body: $errorBody")
            emit(ResultState.Error("Server Error: ${e.code()} - $errorBody"))

        } catch (e: Exception) {
            Log.e("AUTH_REPO", "Exception: ${e.message}", e)
            emit(ResultState.Error(e.localizedMessage ?: "Something went wrong"))
        }
    }

    override fun loginUser(request: LoginRequestData): Flow<ResultState<String>> = flow {
         emit(ResultState.Loading)

        try {
            val response = api.loginUser(request)
            if(response.success && response.data != null){
                tokenManager.saveAccessToken(response.data.accessToken)
                emit(ResultState.Success("Login Successful"))
            }else{
                emit(ResultState.Error("Invalid credentials"))
            }
        }catch (e: CancellationException) {
            throw e
        }catch (e: HttpException) {
            emit(ResultState.Error("Server error: ${e.code()}"))
        }catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage?:"Something went wrong"))
        }
    }

    override fun fetchProfile(): Flow<ResultState<ProfileDto>> = flow{
        emit(ResultState.Loading)

        try {
            val response = api.fetchProfile()
            if(response.success){
                Log.d("User details: ", "${response.data}")
                emit(ResultState.Success(response.data))
            }else{
                emit(ResultState.Error("Failed to fetch data"))
            }
        }catch (e: coil.network.HttpException){
            emit(ResultState.Error("Unauthorized / Server error"))
        }catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage ?: "Something went wrong"))
        }
    }

    override fun fetchNearbyDoctors(
        lat: Double,
        lng: Double,
        distance: Int
    ): Flow<ResultState<List<DoctorDto>>> = flow {
         emit(ResultState.Loading)


        try {
            val response = api.getNearbyDoctors(lat, lng, distance)

            if (response.success) {
                emit(ResultState.Success(response.data))
            } else {
                emit(ResultState.Error("No doctors found"))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.localizedMessage ?: "Something went wrong"))
        }
    }

    override suspend fun addAvailability(
        date: String,
        slots: List<SlotDto>
    ): ResultState<String> {
        return try {
            val response = api.addAvailability(
                AvailabilityRequest(date, slots)
            )
            if (response.success) {
                ResultState.Success(response.message)
            } else {
                ResultState.Error(response.message)
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Something went wrong")
        }
    }

    override suspend fun getAvailability(
        doctorId: String,
        date: String
    ): Flow<ResultState<List<SlotDto>>> = flow {

        emit(ResultState.Loading)

        try {
            val response = api.getDoctorAvailability(doctorId, date)

            if (response.success && response.data != null) {
                emit(ResultState.Success(response.data.slots))
            } else {
                emit(ResultState.Success(emptyList()))
            }

        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Something went wrong"))
        }
    }


}
