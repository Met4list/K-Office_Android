package com.k_office.data.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
