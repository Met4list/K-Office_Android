package com.k_office.domain.use_case

import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.ResponseState
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.data_source.KOfficeDataSource
import com.k_office.domain.model.CurrentUserInfoModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val kOfficeDataSource: KOfficeDataSource,
    private val getCurrentUserInfoDataSource: CurrentUserInfoDataSource
) : BaseUseCase<String, Flow<DataState<CurrentUserInfoModel>>> {
    override suspend fun invoke(telephone: String): Flow<DataState<CurrentUserInfoModel>> =
        flow {
            emit(DataState.Loading)
            supervisorScope {
                val authorizationBonus = async {
                    try {
                        kOfficeDataSource.authorizationBonus(telephone)
                    } catch (t: Throwable) {
                        throw t
                    }
                }.await()

                val balanceBonus = async {
                    try {
                        kOfficeDataSource.getBalanceBonus(telephone)
                    } catch (t: Throwable) {
                        throw t
                    }
                }.await()

                val balanceResult = when (balanceBonus) {
                    is ResponseState.Error -> 0.0
                    ResponseState.Loading -> 0.0
                    is ResponseState.Success<CurrentUserInfoModel> -> balanceBonus.data.sum
                }

                if (authorizationBonus is ResponseState.Success) {
                    val userModel = CurrentUserInfoModel(
                        authorizationBonus.data.bonusCard,
                        authorizationBonus.data.code,
                        authorizationBonus.data.fullName,
                        balanceResult,
                        authorizationBonus.data.telephoneNumber
                    )

                    getCurrentUserInfoDataSource.insertUser(userModel)

                    emit(DataState.Success(userModel))
                } else {
                    emit(DataState.Failure())
                }
            }
        }
}