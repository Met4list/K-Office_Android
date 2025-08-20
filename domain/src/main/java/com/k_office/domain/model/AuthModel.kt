package com.k_office.domain.model

import com.k_office.data.model.UserModel

data class AuthModel(
    val tokens: TokensModel,
    val userModel: UserModel
)
