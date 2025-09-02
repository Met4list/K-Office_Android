package com.k_office.presentation.base.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.core.content.ContextCompat
import javax.inject.Inject

class DirectSMSReader @Inject constructor(private val context: Context) {

    fun getLatestSMS(): String? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val cursor: Cursor? = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("_id", "address", "body", "date"),
            null,
            null,
            "date DESC LIMIT 1"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val body = it.getString(it.getColumnIndexOrThrow("body"))
                return SMSHelper.extractOTP(body)
            }
        }
        return null
    }
}