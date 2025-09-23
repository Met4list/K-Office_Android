package com.k_office.data.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatedUserResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("userModel")
    val userModel: UserResponse?
)
