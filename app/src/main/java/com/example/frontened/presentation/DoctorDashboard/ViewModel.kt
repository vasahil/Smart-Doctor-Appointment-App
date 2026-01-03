package com.example.frontened.presentation.DoctorDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.SlotDto
import com.example.frontened.domain.UseCase.AddAvailabilityUseCase
import com.example.frontened.domain.UseCase.GetAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorDashboardViewModel @Inject constructor(
    private val addAvailabilityUseCase: AddAvailabilityUseCase,
    private val getAvailabilityUseCase: GetAvailabilityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DoctorDashboardState())
    val state: StateFlow<DoctorDashboardState> = _state

    fun addAvailability(date: String, slots: List<SlotDto>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)

            when (val result = addAvailabilityUseCase(date, slots)) {
                is ResultState.Success -> {
                    _state.value = DoctorDashboardState(
                        loading = false,
                        message = result.data
                    )
                }

                is ResultState.Error -> {
                    _state.value = DoctorDashboardState(
                        loading = false,
                        error = result.message
                    )
                }

                else -> Unit
            }
        }
    }

    fun loadAvailability(doctorId: String, date: String) {
        viewModelScope.launch {
            getAvailabilityUseCase(doctorId, date).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _state.value = _state.value.copy(
                            loadingSlots = true,
                            slotsError = null
                        )
                    }

                    is ResultState.Success -> {
                        _state.value = _state.value.copy(
                            loadingSlots = false,
                            bookedSlots = result.data ?: emptyList(),
                            slotsError = null
                        )
                    }

                    is ResultState.Error -> {
                        _state.value = _state.value.copy(
                            loadingSlots = false,
                            slotsError = result.message,
                            bookedSlots = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null, error = null)
    }
}
