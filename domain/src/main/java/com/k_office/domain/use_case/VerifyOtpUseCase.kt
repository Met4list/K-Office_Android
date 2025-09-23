package com.k_office.domain.use_case

import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.TokenStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.UIText
import com.k_office.domain.base.extractServerErrorMessage
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.CurrentUserModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val currentUserStorage: CurrentUserStorage,
    private val tokenStorage: TokenStorage,
) : BaseUseCase<Pair<String, String>, Flow<DataState<CurrentUserModel>>> {
    override suspend fun invoke(request: Pair<String, String>): Flow<DataState<CurrentUserModel>> =
        channelFlow {
            try {
                val telephoneNumber = request.first
                val otp = request.second
                send(DataState.Loading)

                if (otp.isEmpty()) {
                    send(DataState.Failure(UIText.getDefaultErrorMessage()))
                    return@channelFlow
                }
                val response = async {
                    authDataSource.verifyOtp(telephoneNumber, otp)
                }.await()

                currentUserStorage.insertUser(response.user)

                tokenStorage.saveTokens(
                    response.tokens.accessToken,
                    response.expiresIn
                )

                val currentUserModel = CurrentUserMapper.mapTo(response.user)

                send(DataState.Success(currentUserModel))
            } catch (t: HttpException) {
                send(DataState.Failure(t.extractServerErrorMessage()))
            } finally {
                send(DataState.Default)
            }
        }
}