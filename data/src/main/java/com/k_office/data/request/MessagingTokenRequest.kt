package com.k_office.data.request

import com.google.gson.annotations.SerializedName

data class MessagingTokenRequest(
    @SerializedName("token")
    val fcmToken: String,
    @SerializedName("user_id")
    val userId: String
)
