package com.k_office.presentation.base.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

fun Context.openBrowserPage(link: String?) {
    if (link.isNullOrEmpty()) return
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
    startActivity(browserIntent)
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

    val gmmIntentUri = if (placeName.isNullOrEmpty()) {
        Uri.parse("google.navigation:q=$latitude,$longitude")
    } else {
        Uri.parse("google.navigation:q=$latitude,$longitude($placeName)")
    }

    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    if (mapIntent.resolveActivity(packageManager) != null) {
        startActivity(mapIntent)
    } else {
        Toast.makeText(this, "Google Maps app not found. Opening in browser...", Toast.LENGTH_LONG).show()
        val webMapIntent = Intent(Intent.ACTION_VIEW,
            Uri.parse("https://maps.google.com/?q=$latitude,$longitude"))
        if (webMapIntent.resolveActivity(packageManager) != null) {
            startActivity(webMapIntent)
        } else {
            Toast.makeText(this, "No app found to open maps.", Toast.LENGTH_SHORT).show()
        }
    }
}