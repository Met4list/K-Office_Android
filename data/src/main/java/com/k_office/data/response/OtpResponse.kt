package com.k_office.data.response


import com.google.gson.annotations.SerializedName

data class OtpResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("expiresIn")
    val expiresIn: Long,
    @SerializedName("message")
    val message: String,
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    @SerializedName("user")
    val user: UserResponse
)