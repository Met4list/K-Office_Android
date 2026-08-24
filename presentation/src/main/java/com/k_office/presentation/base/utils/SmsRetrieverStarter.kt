package com.k_office.presentation.base.utils

import android.content.Context
import com.google.android.gms.auth.api.phone.SmsRetriever
import timber.log.Timber

fun Context.startSmsRetriever() {
    try {
        SmsRetriever.getClient(this)
            .startSmsRetriever()
            .addOnSuccessListener { Timber.d("SMS Retriever started") }
            .addOnFailureListener { e -> Timber.e(e, "Failed to start SMS Retriever") }
    } catch (e: Exception) {
        Timber.e(e, "Exception starting SMS Retriever")
    }
}
