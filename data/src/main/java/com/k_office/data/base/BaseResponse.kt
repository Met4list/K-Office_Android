package com.k_office.data.base

import com.google.gson.annotations.SerializedName

class BaseResponse<T>(
    @SerializedName("Success")
    val success: Boolean,
    @SerializedName("Message")
    val message: String,
    @SerializedName("Reply")
    val reply: List<T>
)