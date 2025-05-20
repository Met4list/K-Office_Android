package com.k_office.data.response

import com.google.gson.annotations.SerializedName

class CardInformationResponse(
    @SerializedName("BonusCard")
    val bonusCard: String,
    @SerializedName("Kod")
    val code: String
)