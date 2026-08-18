package com.k_office.data.storage

interface TokenStorage {

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    // expiresInMinutes — термін access-токена з бекенду (у хвилинах)
    suspend fun saveTokens(accessToken: String, expiresInMinutes: Long, refreshToken: String? = null)

    suspend fun clearTokens()

    suspend fun isTokenExpired(): Boolean

    suspend fun isAccessTokenExpiringSoon(thresholdMillis: Long = 5 * 60 * 1000L): Boolean

    suspend fun getExpiryTimeMillis(): Long
}