package com.k_office.domain.data_source

import com.k_office.data.storage.TokenStorage
import com.k_office.domain.model.TokensModel

interface TokenDataSource {

    suspend fun getAccessToken(): String?

    suspend fun isTokenExpired(): Boolean

    suspend fun insertTokens(tokens: TokensModel)

    suspend fun clearTokens()

    class Base(private val tokenStorage: TokenStorage) :
        TokenDataSource {

        override suspend fun getAccessToken(): String? {
            return tokenStorage.getAccessToken()
        }

        override suspend fun clearTokens() {
            tokenStorage.clearTokens()
        }

        override suspend fun isTokenExpired(): Boolean {
            return tokenStorage.isTokenExpired()
        }

        override suspend fun insertTokens(tokens: TokensModel) {
            val remainingMinutes = ((tokenStorage.getExpiryTimeMillis() - System.currentTimeMillis()) / 60_000L)
                .coerceAtLeast(1)
            tokenStorage.saveTokens(tokens.accessToken, remainingMinutes, tokens.refreshToken)
        }
    }
}