package com.example.frontened.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri





fun buildGoogleMapsRouteUrl(
    patientLat: Double,
    patientLng: Double,
    doctorLat: Double,
    doctorLng: Double
): String {
    return "https://www.google.com/maps/dir/?api=1" +
            "&origin=$patientLat,$patientLng" +
            "&destination=$doctorLat,$doctorLng" +
            "&travelmode=driving"
}



@SuppressLint("QueryPermissionsNeeded")
fun openGoogleMaps(context: Context, routeUrl: String) {
    val uri = Uri.parse(routeUrl)

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}


//@SuppressLint("QueryPermissionsNeeded")
//fun openGoogleMaps(
//    context: Context,
//    patientLat: Double,
//    patientLng: Double,
//    doctorLat: Double,
//    doctorLng: Double
//) {
//    val uri = ("https://www.google.com/maps/dir/?api=1" +
//            "&origin=$patientLat,$patientLng" +
//            "&destination=$doctorLat,$doctorLng" +
//            "&travelmode=driving").toUri()
//
//    val intent = Intent(Intent.ACTION_VIEW, uri)
//    intent.setPackage("com.google.android.apps.maps")
//
//    if (intent.resolveActivity(context.packageManager) != null) {
//        context.startActivity(intent)
//    } else {
//        // fallback browser
//        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
//    }
//}
