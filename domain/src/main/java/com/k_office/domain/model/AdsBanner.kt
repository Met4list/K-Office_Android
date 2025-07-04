package com.k_office.domain.model

import com.google.gson.annotations.SerializedName

data class AdsBanner(
    @SerializedName("link")
    val link: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("alt_text")
    val altText: String
)