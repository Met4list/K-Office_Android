package com.k_office.data.response

import com.google.gson.annotations.SerializedName

class BonusCardResponse(
    @SerializedName("BonusCard")
    val bonusCard: String,
    @SerializedName("Kod")
    val code: String,
    @SerializedName("Telephone")
    val telephoneNumber: String,
    @SerializedName("Name")
    val fullName: String,
    @SerializedName("Adress")
    val address: String
)