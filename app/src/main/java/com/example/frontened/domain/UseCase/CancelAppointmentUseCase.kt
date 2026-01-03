package com.example.frontened.domain.UseCase

import com.example.frontened.domain.repo.AppointmentRepository
import javax.inject.Inject

class CancelAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    operator fun invoke(id: String) =
        repository.cancelAppointment(id)
}
