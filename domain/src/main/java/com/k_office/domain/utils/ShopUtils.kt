package com.k_office.domain.utils

import android.location.Location
import com.k_office.domain.model.Shop

fun List<Shop>.withCalculatedDistances(userLocation: Location?): List<Shop> {
    return if (userLocation == null) {
        this.map { it.copy(distance = "Calculating...") }
    } else {
        this.map { shop ->
            val storeLocation = Location("").apply {
                latitude = shop.latLng.latitude
                longitude = shop.latLng.longitude
            }
            val distanceInMeters = userLocation.distanceTo(storeLocation)
            val distanceInKm = distanceInMeters / 1000.0

            val formattedDistance = if (distanceInKm >= 1.0) {
                "%.1f км".format(distanceInKm)
            } else {
                "%.0f м".format(distanceInMeters)
            }
            shop.copy(distance = formattedDistance)
        }
    }
}