package com.k_office.data.request

import com.google.gson.annotations.SerializedName

data class RegisterTokenRequest(
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("fcm_token")
    val fcmToken: String
)