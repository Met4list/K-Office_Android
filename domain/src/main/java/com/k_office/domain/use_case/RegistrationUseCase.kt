package com.k_office.domain.use_case

import com.k_office.domain.base.UIText
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.ResponseState
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.data_source.KOfficeDataSource
import com.k_office.domain.model.CurrentUserInfoModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val kOfficeDataSource: KOfficeDataSource,
    private val getCurrentUserInfoDataSource: CurrentUserInfoDataSource
) : BaseUseCase<Map<String, String>, Flow<DataState<CurrentUserInfoModel>>> {
    override suspend fun invoke(request: Map<String, String>): Flow<DataState<CurrentUserInfoModel>> =
        flow {
            emit(DataState.Loading)
            coroutineScope {
                val fullName = request["fullName"]
                val phoneNumber = request["telephoneNumber"]
                val address = request["address"]
                val balanceBonus = phoneNumber?.let {
                    async {
                        kOfficeDataSource.registrationBonus(
                            it,
                            fullName.toString(),
                            address.toString()
                        )
                    }.await()
                }
                if (balanceBonus != null && balanceBonus is ResponseState.Success) {
                    val userModel = balanceBonus?.data?.let {
                        CurrentUserInfoModel(
                            bonusCard = it.bonusCard,
                            code = balanceBonus.data.code,
                            name = fullName.toString(),
                            sum = 0.0,
                            telephone = phoneNumber
                        )
                    }
                    userModel?.let {
                        getCurrentUserInfoDataSource.insertUser(it)
                        emit(DataState.Success(it))
                    }
                } else {
                    if (balanceBonus != null && balanceBonus is ResponseState.Error) {
                        emit(DataState.Failure(UIText.DynamicString(balanceBonus.throwable?.localizedMessage.toString())))
                    } else {
                        emit(DataState.Failure())
                    }
                }
            }
        }
}