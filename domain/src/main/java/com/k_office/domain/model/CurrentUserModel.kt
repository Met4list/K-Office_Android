package com.k_office.domain.model

data class CurrentUserModel(
    val address: String,
    val bonusCard: String?,
    val code: String?,
    val createdAt: String,
    val id: String,
    val name: String,
    val telephone: String,
    val sum: Float?
)