package com.k_office.domain.use_case

import com.k_office.data.api.KOfficeApi
import com.k_office.data.request.BaseRequest
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.CurrentUserInfoModel
import javax.inject.Inject

class GetCurrentUserInfoUseCase @Inject constructor(
    private val kOfficeApi: KOfficeApi,
    private val currentUserInfoDataSource: CurrentUserInfoDataSource
) : BaseUseCase<String, CurrentUserInfoModel> {

    override suspend fun invoke(request: String): CurrentUserInfoModel {
        if (currentUserInfoDataSource.getUser() == null) {
            val phoneNumber = BaseRequest.Parameters(listOf(request))
            val balanceBonus =
                kOfficeApi.getBalanceBonus(BaseRequest.BalanceRequest(parameters = phoneNumber))

            if (!balanceBonus.success) {
                return throw Exception("Current user is not available. Please try again.")
            }
            val response = balanceBonus.reply.first()
            val model = CurrentUserMapper.mapTo(response)

            currentUserInfoDataSource.insertUser(model)
            return model
        } else {
            return currentUserInfoDataSource.getUser()!!
        }
    }
}