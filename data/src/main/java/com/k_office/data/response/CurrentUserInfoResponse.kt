package com.k_office.data.response


import com.google.gson.annotations.SerializedName

data class CurrentUserInfoResponse(
    @SerializedName("BonusCard")
    val bonusCard: String,
    @SerializedName("Kod")
    val code: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Sum")
    val sum: Double,
    @SerializedName("Telephone")
    val telephone: String
)