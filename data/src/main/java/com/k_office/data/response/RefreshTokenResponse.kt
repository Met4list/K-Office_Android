package com.k_office.data.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    @SerializedName("expiresIn")
    val expiresIn: Long
)
