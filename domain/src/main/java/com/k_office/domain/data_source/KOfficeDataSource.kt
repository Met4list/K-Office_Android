package com.k_office.domain.data_source

import com.k_office.data.api.KOfficeApi
import com.k_office.data.request.BaseRequest
import com.k_office.domain.base.ResponseState
import com.k_office.domain.mapper.BonusCardMapper
import com.k_office.domain.mapper.CardInfoMapper
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.BonusCardModel
import com.k_office.domain.model.CardInformationModel
import com.k_office.domain.model.CurrentUserInfoModel

interface KOfficeDataSource {

    suspend fun getBalanceBonus(telephone: String): ResponseState<CurrentUserInfoModel>

    suspend fun registrationBonus(
        telephone: String,
        name: String,
        address: String
    ): ResponseState<CardInformationModel>

    suspend fun authorizationBonus(
        telephone: String,
        name: String,
        address: String
    ): ResponseState<BonusCardModel>

    class Base(private val api: KOfficeApi) : KOfficeDataSource {

        override suspend fun getBalanceBonus(telephone: String): ResponseState<CurrentUserInfoModel> {
            return try {
                val response = api.getBalanceBonus(
                    BaseRequest.BalanceRequest(
                        parameters = BaseRequest.Parameters(listOf(telephone))
                    )
                )
                if (response.success) {
                    val reply = response.reply.map { CurrentUserMapper.mapTo(it) }.first()
                    ResponseState.Success(reply)
                } else {
                    ResponseState.Error(Throwable(response.message))
                }
            } catch (t: Throwable) {
                ResponseState.Error(t)
            }
        }

        override suspend fun registrationBonus(
            telephone: String,
            name: String,
            address: String
        ): ResponseState<CardInformationModel> {
            return try {
                val response = api.registrationBonus(
                    BaseRequest.RegistrationRequest(
                        data = listOf(
                            BaseRequest.Data(
                                telephone = telephone,
                                name = name,
                                address = address
                            )
                        )
                    )
                )
                if (response.success) {
                    val reply = response.reply.map { CardInfoMapper.mapTo(it) }.first()
                    ResponseState.Success(reply)
                } else {
                    ResponseState.Error(Throwable(response.message))
                }
            } catch (t: Throwable) {
                ResponseState.Error(t)
            }
        }

        override suspend fun authorizationBonus(
            telephone: String,
            name: String,
            address: String
        ): ResponseState<BonusCardModel> {
            return try {
                val response = api.authorizationBonus(
                    BaseRequest.Authorization(
                        data = listOf(
                            BaseRequest.Data(
                                telephone = telephone,
                                name = name,
                                address = address
                            )
                        )
                    )
                )
                if (response.success) {
                    val reply = response.reply.map { BonusCardMapper.mapTo(it) }.first()
                    ResponseState.Success(reply)
                } else {
                    ResponseState.Error(Throwable(response.message))
                }
            } catch (t: Throwable) {
                ResponseState.Error(t)
            }
        }
    }
}