package com.example.frontened.domain.UseCase

import com.example.frontened.domain.repo.AppointmentRepository
import javax.inject.Inject

class GetDoctorAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {

    operator fun invoke() =
        repository.getDoctorAppointments()
}