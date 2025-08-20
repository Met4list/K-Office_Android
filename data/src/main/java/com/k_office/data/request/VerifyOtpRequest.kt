package com.k_office.data.request

data class VerifyOtpRequest(
    val telephone: String,
    val otp: String
)
