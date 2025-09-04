package com.k_office.data.model


data class UserRefreshModel(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
    val user: UserModel
)

data class UserModel(
    val address: String?,
    val bonusCard: String?,
    val code: String?,
    val createdAt: String,
    val id: String,
    val name: String,
    val telephone: String,
    val sum: Float?
)