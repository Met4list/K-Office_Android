package com.k_office.domain.mapper

import com.k_office.data.model.UserModel
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.CurrentUserModel

object CurrentUserMapper: Mapper<UserModel, CurrentUserModel> {

    override fun mapTo(response: UserModel): CurrentUserModel = with(response) {
        CurrentUserModel(
            address,
            bonusCard,
            code,
            createdAt,
            id,
            name,
            telephone,
            sum
        )
    }
}