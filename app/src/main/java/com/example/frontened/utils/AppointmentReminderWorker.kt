package com.example.frontened.utils


import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class AppointmentReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {

        val doctorName = inputData.getString("doctorName") ?: return Result.failure()
        val date = inputData.getString("date") ?: return Result.failure()
        val time = inputData.getString("time") ?: return Result.failure()
        val routeUrl = inputData.getString("routeUrl") ?: ""

        NotificationHelper.showAppointmentBooked(
            context = applicationContext,
            doctorName = doctorName,
            date = date,
            time = time,
            routeUrl = routeUrl
        )

        return Result.success()
    }
}


object AppointmentReminderScheduler {

    fun scheduleOneHourBefore(
        context: Context,
        appointmentDateTime: LocalDateTime,
        doctorName: String,
        routeUrl: String
    ) {
        val appointmentMillis =
            appointmentDateTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

        val triggerTime = appointmentMillis - TimeUnit.HOURS.toMillis(1)
        val delay = triggerTime - System.currentTimeMillis()

        if (delay <= 0) return // appointment too soon

        val data = workDataOf(
            "doctorName" to doctorName,
            "date" to appointmentDateTime.toLocalDate().toString(),
            "time" to appointmentDateTime.toLocalTime().toString(),
            "routeUrl" to routeUrl
        )

        val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}