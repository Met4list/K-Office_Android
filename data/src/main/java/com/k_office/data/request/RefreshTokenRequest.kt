package com.k_office.data.request

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(@SerializedName("refreshToken") val refreshToken: String)
