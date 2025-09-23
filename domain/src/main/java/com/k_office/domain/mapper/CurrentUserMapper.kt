package com.k_office.domain.mapper

import com.k_office.data.model.UserModel
import com.k_office.data.model.UserRefreshModel
import com.k_office.data.response.UserData
import com.k_office.data.response.UserRefreshResponse
import com.k_office.data.response.UserResponse
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

    fun mapTo(response: UserResponse): UserModel = with(response) {
        UserModel(
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

    fun mapTo(response: UserRefreshResponse): UserRefreshModel = with(response.user) {
        UserRefreshModel(
            message = response.message,
            accessToken = response.accessToken,
            expiresIn = response.expiresIn,
            user = UserModel(
                address = null,
                bonusCard,
                code,
                createdAt = "",
                id,
                name,
                telephone,
                sum.toFloat()
            )
        )
    }

    fun mapTo(model: CurrentUserModel): UserModel = with(model) {
        UserModel(
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