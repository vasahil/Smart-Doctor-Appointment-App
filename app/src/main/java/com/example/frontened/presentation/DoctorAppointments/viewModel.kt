package com.example.frontened.presentation.DoctorAppointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.DoctorAppointmentDto
import com.example.frontened.domain.UseCase.GetDoctorAppointmentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorAppointmentsViewModel @Inject constructor(
    private val getDoctorAppointmentsUseCase: GetDoctorAppointmentsUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<ResultState<List<Any>>>(ResultState.Loading)

    val state: StateFlow<ResultState<List<Any>>> = _state

    fun loadAppointments() {
        viewModelScope.launch {
            getDoctorAppointmentsUseCase().collect {
                _state.value = it
            }
        }
    }
}