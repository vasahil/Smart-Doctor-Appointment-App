package com.example.frontened.presentation.ProfileScreen

import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontened.common.ResultState
import com.example.frontened.domain.UseCase.ProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCase: ProfileUseCase
): ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    fun fetchProfile() {
        viewModelScope.launch {
            profileUseCase().collect {result->
                when(result) {
                    is ResultState.Loading -> {
                        _state.value = ProfileState(
                            loading = true
                        )
                    }

                    is ResultState.Success -> {
                        _state.value = ProfileState(loading = false, data = result.data)
                    }

                    is ResultState.Error -> {
                        _state.value = _state.value.copy(
                            loading = false,
                            error = result.message
                        )
                    }
                }

            }
        }
    }
}