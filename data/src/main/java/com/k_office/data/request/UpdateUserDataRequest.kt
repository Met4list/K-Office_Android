package com.k_office.data.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDataRequest(
    val name: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String
)
