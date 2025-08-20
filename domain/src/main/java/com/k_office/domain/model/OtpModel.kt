package com.k_office.domain.model

import com.k_office.data.model.UserModel

data class OtpModel(
    val expiresIn: Long,
    val tokens: TokensModel,
    val user: UserModel
)