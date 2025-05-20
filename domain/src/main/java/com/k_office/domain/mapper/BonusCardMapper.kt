package com.k_office.domain.mapper

import com.k_office.data.response.BonusCardResponse
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.BonusCardModel

object BonusCardMapper: Mapper<BonusCardResponse, BonusCardModel> {
    override fun mapTo(response: BonusCardResponse): BonusCardModel {
        return BonusCardModel(response.bonusCard, response.code)
    }

    override fun mapFrom(model: BonusCardModel): BonusCardResponse {
        return BonusCardResponse(model.bonusCard, model.code)
    }
}