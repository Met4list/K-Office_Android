package com.k_office.presentation.base.utils

import android.annotation.SuppressLint
import timber.log.Timber
import java.util.regex.Pattern

object SMSHelper {

    private const val TAG = "SMSHelper"

    private val otpRegex = Regex("""\b(\d{4,6})\b""")

    fun extractOTP(text: String): String {
        return otpRegex.find(text)?.groupValues?.getOrNull(1) ?: ""
    }

    fun isValidOTP(otp: String): Boolean = otp.length == 4 && otp.all { it.isDigit() }
}