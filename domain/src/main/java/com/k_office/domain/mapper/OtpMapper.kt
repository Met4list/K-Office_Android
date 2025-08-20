package com.k_office.domain.mapper

import com.k_office.data.response.OtpResponse
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.OtpModel
import com.k_office.domain.model.TokensModel
import com.k_office.data.model.UserModel

object OtpMapper: Mapper<OtpResponse, OtpModel> {
    override fun mapTo(response: OtpResponse): OtpModel = with(response) {
        OtpModel(
            expiresIn,
            tokens = TokensModel(
                accessToken,
                refreshToken
            ),
            user = with(user) {
                UserModel(
                    address,
                    bonusCard,
                    code,
                    createdAt,
                    id,
                    name,
                    telephone,
                    null
                )
            }
        )
    }
}