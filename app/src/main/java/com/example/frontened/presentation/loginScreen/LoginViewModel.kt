package com.example.frontened.presentation.loginScreen

import dagger.hilt.android.lifecycle.HiltViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.LoginRequestData
import com.example.frontened.domain.UseCase.LoginUserUseCase
import com.example.frontened.domain.UseCase.RegisterUserUseCase

import com.example.frontened.domain.repo.AuthRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    fun login(request: LoginRequestData) {
        viewModelScope.launch {
            loginUserUseCase(request).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _state.value = _state.value.copy(
                            loading = true,
                            error = null,
                            message = null
                        )
                    }

                    is ResultState.Success -> {
                        _state.value = _state.value.copy(
                            loading = false,
                            message = result.data,
                            error = null
                        )
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

    fun clearState() {
        _state.value = LoginState()
    }

}
