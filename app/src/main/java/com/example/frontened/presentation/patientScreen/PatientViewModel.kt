package com.example.frontened.presentation.patientScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.AppointmentDto
import com.example.frontened.data.dto.DoctorDto
import com.example.frontened.data.dto.NearbyDoctorResponse
import com.example.frontened.data.dto.SlotDto
import com.example.frontened.data.repository.Auth.AuthRepoImpl
import com.example.frontened.domain.UseCase.NearbyDoctorUseCase
import com.example.frontened.domain.repo.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.frontened.data.dto.DoctorAvailabilityDto
import com.example.frontened.domain.UseCase.CancelAppointmentUseCase


@HiltViewModel
class PatientViewModel @Inject constructor(
    private val nearbyDoctorUseCase: NearbyDoctorUseCase,
    private val repo: AuthRepoImpl,
    private val appointmentRepo: AppointmentRepository,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<ResultState<List<DoctorDto>>>(ResultState.Loading)

    val state: StateFlow<ResultState<List<DoctorDto>>> = _state

    private val _state1 =
        MutableStateFlow<ResultState<List<SlotDto>>>(ResultState.Loading)

    val state1: StateFlow<ResultState<List<SlotDto>>> = _state1

    private val _bookingState =
        MutableStateFlow<ResultState<String>?>(null)
    val bookingState: StateFlow<ResultState<String>?> = _bookingState

    private val _appointmentsState =
        MutableStateFlow<ResultState<List<AppointmentDto>>>(ResultState.Loading)

    val appointmentsState: StateFlow<ResultState<List<AppointmentDto>>> =
        _appointmentsState

    fun loadNearbyDoctors(lat: Double, lng: Double, distance: Int) {
        viewModelScope.launch {
            nearbyDoctorUseCase(lat, lng, distance)
                .collect { result ->
                    _state.value = result   // ✅ handle ALL states
                }
        }
    }



    fun loadAvailability(doctorId: String, date: String) {
        viewModelScope.launch {
            Log.d("AVAILABILITY", "doctorId=$doctorId, date=$date")

            _state1.value = ResultState.Loading

            repo.getAvailability(doctorId, date)
                .collect { result ->
                    Log.d("AVAILABILITY_VM", "state = $result")
                    _state1.value = result
                }
        }
    }



    fun bookAppointment(
        doctorId: String,
        date: String,
        startTime: String,
        endTime: String
    ) {
        viewModelScope.launch {
            appointmentRepo.bookAppointment(
                doctorId, date, startTime, endTime
            ).collect {
                _bookingState.value = it
            }
        }
    }




    fun loadMyAppointments() {
        viewModelScope.launch {
            appointmentRepo.getMyAppointments()
                .collect { _appointmentsState.value = it }
        }
    }

    private val _cancelState = MutableStateFlow<ResultState<String>?>(null)
    val cancelState: StateFlow<ResultState<String>?> = _cancelState

    fun cancelAppointment(id: String) {
        viewModelScope.launch {
            cancelAppointmentUseCase(id).collect {
                _cancelState.value = it

                if (it is ResultState.Success) {
                    loadMyAppointments()
                }
            }
        }
    }

}
