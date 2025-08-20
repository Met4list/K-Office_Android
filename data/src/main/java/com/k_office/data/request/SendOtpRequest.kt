package com.k_office.data.request

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    @SerializedName("phone")
    val phone: String,
    @SerializedName("fcmToken")
    val fcmToken: String
)
