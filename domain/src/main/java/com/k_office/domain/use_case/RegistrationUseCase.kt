package com.k_office.domain.use_case

import com.k_office.data.model.UserModel
import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.TokenStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.model.RegistrationModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val currentUserStorage: CurrentUserStorage,
    private val tokenStorage: TokenStorage,
) : BaseUseCase<Map<String, String>, Flow<DataState<UserModel>>> {
    override suspend fun invoke(request: Map<String, String>): Flow<DataState<UserModel>> =
        flow {
            emit(DataState.Loading)
            coroutineScope {
                val fullName = request["fullName"].toString()
                val phoneNumber = request["telephoneNumber"].toString()
                val address = request["address"].toString()

                if (fullName.isNullOrEmpty() || phoneNumber.isNullOrEmpty() || address.isNullOrEmpty()) {
                    emit(DataState.Failure())
                    return@coroutineScope
                }

                val response = async {
                    authDataSource.register(
                        RegistrationModel(phoneNumber, fullName, address)
                    )
                }.await()

                if (response == null) {
                    emit(DataState.Failure())
                    return@coroutineScope
                }

                currentUserStorage.insertUser(
                    response.user
                )
                response.tokens.apply {
                    tokenStorage.saveTokens(accessToken, refreshToken, response.expiresIn)
                }

                emit(DataState.Success(response.user))
            }
            emit(DataState.Default)
        }
}