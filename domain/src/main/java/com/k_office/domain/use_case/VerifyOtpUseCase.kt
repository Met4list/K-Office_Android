package com.k_office.domain.use_case

import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.TokenStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.CurrentUserModel
import com.k_office.domain.model.OtpModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val currentUserStorage: CurrentUserStorage,
    private val tokenStorage: TokenStorage,
) : BaseUseCase<Pair<String, String>, Flow<DataState<CurrentUserModel>>> {
    override suspend fun invoke(request: Pair<String, String>): Flow<DataState<CurrentUserModel>> = flow {
        val telephoneNumber = request.first.drop(1)
        val otp = request.second
        emit(DataState.Loading)
        if (telephoneNumber.isEmpty() || otp.isEmpty()) {
            emit(DataState.Failure())
            return@flow
        }
        coroutineScope {
            val response = async {
                authDataSource.verifyOtp(telephoneNumber, otp)
            }.await()

            currentUserStorage.insertUser(response.user)

            tokenStorage.saveTokens(
                response.tokens.accessToken,
                response.tokens.refreshToken,
                response.expiresIn
            )

            val currentUserModel = CurrentUserMapper.mapTo(response.user)

            emit(DataState.Success(currentUserModel))
        }
        emit(DataState.Default)
    }
}