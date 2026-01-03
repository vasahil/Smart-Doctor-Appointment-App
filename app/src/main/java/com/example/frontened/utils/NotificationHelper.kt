package com.example.frontened.utils


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.frontened.R

object NotificationHelper {

    private const val CHANNEL_ID = "appointment_channel"

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showAppointmentBooked(
        context: Context,
        doctorName: String,
        date: String,
        time: String,
        routeUrl: String
    ) {

        createChannel(context)

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(routeUrl)
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24) // add icon
            .setContentTitle("Appointment Confirmed")
            .setContentText("$doctorName • $time on $date")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Appointment with Dr $doctorName\n$date at $time\nTap to get directions")
            )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(
                R.drawable.outline_map_pin_review_24,
                "Get Directions",
                pendingIntent
            )
            .build()

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Appointments",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Appointment booking notifications"
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
