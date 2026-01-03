package com.example.frontened.domain.UseCase

import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.ProfileDto

import com.example.frontened.domain.repo.AuthRepository
import com.example.frontened.domain.repo.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val repo: AuthRepository
) {

     operator fun invoke() : Flow<ResultState<ProfileDto>> {
         return repo.fetchProfile()
     }
}