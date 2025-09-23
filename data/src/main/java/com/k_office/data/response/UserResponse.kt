package com.k_office.data.response


import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("bonusCard")
    val bonusCard: String?,
    @SerializedName("code")
    val code: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("telephone")
    val telephone: String,
    @SerializedName("sum")
    val sum: Float?
)