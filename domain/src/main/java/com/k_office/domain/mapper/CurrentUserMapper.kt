package com.k_office.domain.mapper

import com.k_office.data.response.CurrentUserInfoResponse
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.CurrentUserInfoModel

object CurrentUserMapper: Mapper<CurrentUserInfoResponse, CurrentUserInfoModel> {

    override fun mapTo(response: CurrentUserInfoResponse): CurrentUserInfoModel =
        CurrentUserInfoModel(
            bonusCard = response.bonusCard,
            code = response.code,
            name = response.name,
            sum = response.sum,
            telephone = response.telephone
        )

    override fun mapFrom(model: CurrentUserInfoModel): CurrentUserInfoResponse =
        CurrentUserInfoResponse(
            bonusCard = model.bonusCard,
            code = model.code,
            name = model.name,
            sum = model.sum,
            telephone = model.telephone
        )
}