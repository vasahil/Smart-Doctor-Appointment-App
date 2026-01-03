package com.example.frontened.domain.UseCase

import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.LoginRequestData
import com.example.frontened.data.dto.RegisterRequestDto
import com.example.frontened.data.repository.Auth.AuthRepoImpl
import com.example.frontened.domain.repo.AuthRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class RegisterUserUseCase @Inject constructor(
    private val repo: AuthRepository
) {

    suspend operator fun invoke(
        request: RegisterRequestDto
    ): Flow<ResultState<String>> {
        return repo.registerUser(request)
    }

//    suspend operator fun invoke(
//        request: LoginRequestData
//    ): Flow<ResultState<String>> {
//        return repo.loginUser(request)
//    }

}


class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(
        request: LoginRequestData
    ): Flow<ResultState<String>> {
        return authRepository.loginUser(request)
    }
}