package com.k_office.domain.mapper

import com.k_office.data.response.CardInformationResponse
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.CardInformationModel

object CardInfoMapper : Mapper<CardInformationResponse, CardInformationModel> {
    override fun mapTo(response: CardInformationResponse): CardInformationModel = with(response) {
         CardInformationModel(
            bonusCard, code
        )
    }

    override fun mapFrom(model: CardInformationModel): CardInformationResponse = with(model) {
        CardInformationResponse(bonusCard, code)
    }
}