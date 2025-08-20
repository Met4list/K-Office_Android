package com.k_office.domain.use_case

import com.google.firebase.messaging.FirebaseMessaging
import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.TokenStorage
import com.k_office.domain.data_source.AuthDataSource
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val currentUserStorage: CurrentUserStorage,
    private val tokenStorage: TokenStorage,
    private val authDataSource: AuthDataSource,
    private val firebaseMessaging: FirebaseMessaging
) {

    operator suspend fun invoke(onComplete: (Boolean) -> Unit) = coroutineScope {
        currentUserStorage.clear()
        val refreshToken = tokenStorage.getRefreshToken()
        if (!refreshToken.isNullOrEmpty()) {
            authDataSource.logout(refreshToken)
        }
        tokenStorage.clearTokens()
        firebaseMessaging.deleteToken()
    }.addOnCompleteListener {
        onComplete.invoke(true)
    }
}