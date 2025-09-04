package com.k_office.data.storage

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenStorageImpl(private val sharedPreferences: SharedPreferences): TokenStorage {

    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val TOKEN_EXPIRY_KEY = "token_expiry"
    }

    override suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    override suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        expiryTimeMillis: Long
    ) = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(ACCESS_TOKEN_KEY, accessToken)
            .putString(REFRESH_TOKEN_KEY, refreshToken)
            .putLong(TOKEN_EXPIRY_KEY, expiryTimeMillis)
            .apply()
    }

    override suspend fun clearTokens() = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .remove(ACCESS_TOKEN_KEY)
            .remove(REFRESH_TOKEN_KEY)
            .remove(TOKEN_EXPIRY_KEY)
            .apply()
    }

    override suspend fun isTokenExpired(): Boolean = withContext(Dispatchers.IO) {
        val expiryTime = sharedPreferences.getLong(TOKEN_EXPIRY_KEY, 0)
        System.currentTimeMillis() >= expiryTime
    }

    override suspend fun getExpiryTimeMillis(): Long = withContext(Dispatchers.IO) {
        sharedPreferences.getLong(TOKEN_EXPIRY_KEY, 0L)
    }
}