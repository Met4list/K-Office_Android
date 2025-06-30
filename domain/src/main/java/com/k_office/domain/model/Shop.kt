package com.k_office.domain.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Shop(
    @SerializedName("advantages")
    val advantages: List<String>,
    @SerializedName("full_address")
    val fullAddress: String,
    @SerializedName("latLng")
    val latLng: LatLng,
    @SerializedName("location_details")
    val locationDetails: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("services")
    val services: List<String>,
    @SerializedName("street")
    val street: String,
    val distance: String? = null
): Parcelable