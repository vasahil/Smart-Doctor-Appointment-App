package com.example.frontened.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DoctorDto(
    @SerializedName("_id")
    val id: String?,
    val name: String,
    val email: String,
    val speciality: String,
    val fee: Int,
    val location: LocationDto,
    val distance: Double
): Parcelable

@Serializable
@Parcelize
data class LocationDto(
    val type: String,
    val coordinates: List<Double> // [lng, lat]
): Parcelable

@Serializable
data class NearbyDoctorResponse(
    val success: Boolean,
    val data: List<DoctorDto>
)
