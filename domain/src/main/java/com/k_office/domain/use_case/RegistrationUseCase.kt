package com.k_office.domain.use_case

import com.k_office.data.model.UserModel
import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.TokenStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.UIText
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.model.RegistrationModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val currentUserStorage: CurrentUserStorage,
    private val tokenStorage: TokenStorage,
) : BaseUseCase<Map<String, String>, Flow<DataState<UserModel>>> {
    override suspend fun invoke(request: Map<String, String>): Flow<DataState<UserModel>> =
        channelFlow {
            try {
                send(DataState.Loading)
                val fullName = request["fullName"].toString()
                val phoneNumber = request["telephoneNumber"].toString()

                if (fullName.isNullOrEmpty() || phoneNumber.isNullOrEmpty()) {
                    send(DataState.Failure(UIText.getDefaultErrorMessage()))
                    return@channelFlow
                }

                val response = async {
                    authDataSource.register(
                        RegistrationModel(phoneNumber, fullName)
                    )
                }.await()

                if (response == null) {
                    send(DataState.Failure(UIText.getDefaultErrorMessage()))
                    return@channelFlow
                }

                currentUserStorage.insertUser(
                    response.user
                )
                response.tokens.apply {
                    tokenStorage.saveTokens(accessToken, response.expiresIn, refreshToken)
                }
                send(DataState.Success(response.user))
                send(DataState.Default)
            } catch (t: Throwable) {
                t.printStackTrace()
                send(DataState.Failure(t.toUIText()))
            } finally {
                send(DataState.Default)
            }
        }
}