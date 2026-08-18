package com.k_office.data.response

import com.google.gson.annotations.SerializedName

data class UserRefreshResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("expiresIn")
    val expiresIn: Int,
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    @SerializedName("user")
    val user: UserData
)

data class UserData(
    @SerializedName("id")
    val id: String,
    @SerializedName("telephone")
    val telephone: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("bonusCard")
    val bonusCard: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("sum")
    val sum: Double
)
