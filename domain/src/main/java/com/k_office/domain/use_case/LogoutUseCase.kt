package com.k_office.domain.use_case

import com.google.firebase.messaging.FirebaseMessaging
import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.TokenStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.AuthDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val currentUserStorage: CurrentUserStorage,
    private val tokenStorage: TokenStorage,
    private val authDataSource: AuthDataSource,
    private val firebaseMessaging: FirebaseMessaging
): BaseUseCase<Unit, Flow<DataState<Boolean>>> {
    override suspend fun invoke(request: Unit): Flow<DataState<Boolean>> = channelFlow {
        try {
            send(DataState.Loading)
            currentUserStorage.clear()
            val refreshToken = tokenStorage.getRefreshToken()
            if (!refreshToken.isNullOrEmpty()) {
                authDataSource.logout(refreshToken)
            }
            tokenStorage.clearTokens()
            firebaseMessaging.deleteToken()
            send(DataState.Success(true))
            send(DataState.Default)
        } catch (t: Throwable) {
            send(DataState.Failure(t.toUIText()))
        }
    }
}