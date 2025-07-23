package com.k_office.domain.mapper

import com.k_office.data.response.BonusCardResponse
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.BonusCardModel

object BonusCardMapper: Mapper<BonusCardResponse, BonusCardModel> {
    override fun mapTo(response: BonusCardResponse): BonusCardModel = with(response) {
        BonusCardModel(bonusCard, code, telephoneNumber, fullName, address)
    }

    override fun mapFrom(model: BonusCardModel): BonusCardResponse = with(model) {
        BonusCardResponse(bonusCard, code, telephoneNumber, fullName, address)
    }
}