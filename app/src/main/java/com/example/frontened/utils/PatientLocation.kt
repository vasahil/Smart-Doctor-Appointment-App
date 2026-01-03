package com.example.frontened.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val fusedClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLastLocation(
        onLocation: (Double, Double) -> Unit
    ) {
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("LOCATION", "REAL: ${location.latitude}, ${location.longitude}")
                    onLocation(location.latitude, location.longitude)
                } else {
                    // ✅ fallback for first launch / emulator
                    onLocation(28.628, 77.367)
                }
            }
            .addOnFailureListener {
                // ✅ fallback on error
                onLocation(28.628, 77.367)
            }
    }
}


@SuppressLint("ServiceCast")
fun isLocationEnabled(context: Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}


@Composable
fun RequireGpsEnabled(
    onGpsEnabled: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!isLocationEnabled(context)) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        } else {
            onGpsEnabled()
        }
    }
}
