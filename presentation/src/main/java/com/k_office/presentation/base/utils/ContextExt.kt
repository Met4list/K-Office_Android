package com.k_office.presentation.base.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

fun Context.openBrowserPage(link: String?) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    } catch (e: Exception) {
        Timber.e(e)
        Toast.makeText(
            this,
            "Не вдалося відкрити посилання: ${link}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun Context.findActivity(): AppCompatActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun Context.openGoogleMapsRoute(latitude: Double, longitude: Double, placeName: String? = null) {
    val destination = "$latitude,$longitude"
    val destinationWithLabel = if (placeName.isNullOrBlank()) {
        destination
    } else {
        // Підпис точки без лапок, щоб URI навігації не зламався
        "$destination(${Uri.encode(placeName.replace("\"", ""))})"
    }
    // mode=d — автомобільний маршрут від поточної геопозиції клієнта
    val navigationUri = Uri.parse("google.navigation:q=$destinationWithLabel&mode=d")
    val mapsIntent = Intent(Intent.ACTION_VIEW, navigationUri).apply {
        setPackage("com.google.android.apps.maps")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        startActivity(mapsIntent)
    } catch (e: Exception) {
        Timber.e(e, "Не вдалося відкрити додаток Google Maps")
        // Якщо додаток не встановлено — маршрут у браузері, не просто точка на карті
        val webUri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1&destination=$destination&travelmode=driving"
        )
        try {
            startActivity(
                Intent(Intent.ACTION_VIEW, webUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (webError: Exception) {
            Timber.e(webError)
            Toast.makeText(this, "Не вдалося відкрити Google Maps", Toast.LENGTH_SHORT).show()
        }
    }
}

fun Context.openNotificationSettings() {
    val settingsIntent: Intent? = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
//        .putExtra(Settings.EXTRA_CHANNEL_ID, MY_CHANNEL_ID)
    startActivity(settingsIntent)
}

fun Float.pxToDp(context: Context): Float =
    (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))