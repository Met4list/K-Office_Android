package com.k_office.data.model

import com.google.gson.annotations.SerializedName

data class AuthTokens(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("expiresIn")
    val expiresIn: Long
)
