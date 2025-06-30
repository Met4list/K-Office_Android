package com.k_office.domain.model


import com.google.gson.annotations.SerializedName

data class ShopValuesModel(
    @SerializedName("shops")
    val shops: List<Shop>
)