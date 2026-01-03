package com.example.frontened.data.repository.Auth

import android.util.Log
import coil.network.HttpException
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.ProfileDto
import com.example.frontened.domain.di.AuthApi
import com.example.frontened.domain.di.ProfileApi
import com.example.frontened.domain.repo.AuthRepository
import com.example.frontened.domain.repo.ProfileRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


