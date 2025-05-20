package com.k_office.domain.mapper

import com.k_office.data.response.CardInformationResponse
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.CardInformationModel

object CardInfoMapper : Mapper<CardInformationResponse, CardInformationModel> {
    override fun mapTo(response: CardInformationResponse): CardInformationModel {
        return CardInformationModel(
            response.bonusCard, response.code
        )
    }

    override fun mapFrom(model: CardInformationModel): CardInformationResponse {
        return CardInformationResponse(model.bonusCard, model.code)
    }
}