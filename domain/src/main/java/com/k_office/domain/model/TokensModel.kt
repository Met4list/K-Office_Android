package com.k_office.domain.model

data class TokensModel(
    val accessToken: String,
    val refreshToken: String? = null
)
