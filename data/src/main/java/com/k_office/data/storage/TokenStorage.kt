package com.k_office.data.storage

interface TokenStorage {

    suspend fun getAccessToken(): String?

    suspend fun saveTokens(accessToken: String, expiryTimeMillis: Long)

    suspend fun clearTokens()

    suspend fun isTokenExpired(): Boolean

    suspend fun getExpiryTimeMillis(): Long
}