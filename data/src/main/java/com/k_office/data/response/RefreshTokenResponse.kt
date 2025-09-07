package com.k_office.data.response

data class RefreshTokenResponse(
    val accessToken: String,
    val expiresIn: Long
)
