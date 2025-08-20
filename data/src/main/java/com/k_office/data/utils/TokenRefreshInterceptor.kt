package com.k_office.data.utils

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.k_office.data.api.AuthApiService
import com.k_office.data.model.AuthTokens
import com.k_office.data.request.RefreshTokenRequest
import com.k_office.data.storage.TokenStorage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class TokenRefreshInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApiService: AuthApiService,
    private val localBroadCastManager: LocalBroadcastManager
) : Interceptor {

    private val mutex = Mutex()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // If response is 401 and it's not an auth endpoint, try to refresh token
        if (response.code == 401 && !originalRequest.url.encodedPath.contains("/auth/")) {
            return handleUnauthorized(chain, originalRequest, response)
        }

        return response
    }

    private fun handleUnauthorized(
        chain: Interceptor.Chain,
        originalRequest: Request,
        response: Response
    ): Response {
        response.close()

        return runBlocking {
            mutex.withLock {
                // Check if token was already refreshed by another thread
                val currentToken = tokenStorage.getAccessToken()
                val originalToken = originalRequest.header("Authorization")?.removePrefix("Bearer ")

                if (currentToken != null && currentToken != originalToken) {
                    // Token was already refreshed, retry with new token
                    return@withLock retryRequestWithNewToken(chain, originalRequest, currentToken)
                }

                // Try to refresh token
                val refreshResult = refreshTokenInternal()
                when {
                    refreshResult.isSuccess -> {
                        val newAccessToken = refreshResult.getOrNull()?.accessToken
                        if (newAccessToken != null) {
                            retryRequestWithNewToken(chain, originalRequest, newAccessToken)
                        } else {
                            handleRefreshFailure()
                            chain.proceed(originalRequest)
                        }
                    }
                    else -> {
                        handleRefreshFailure()
                        chain.proceed(originalRequest)
                    }
                }
            }
        }
    }

    private suspend fun refreshTokenInternal(): Result<AuthTokens> {
        return try {
            val refreshToken = tokenStorage.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token available"))

            val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))

            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val authTokens = AuthTokens(dto.accessToken, dto.refreshToken, dto.expiresIn)
                    val expiryTime = System.currentTimeMillis() + (dto.expiresIn * 60 * 1000)
                    tokenStorage.saveTokens(dto.accessToken, dto.refreshToken, expiryTime)
                    Result.success(authTokens)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Token refresh failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun retryRequestWithNewToken(
        chain: Interceptor.Chain,
        originalRequest: Request,
        newAccessToken: String
    ): Response {
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()

        return chain.proceed(newRequest)
    }

    private fun handleRefreshFailure() {
        runBlocking { tokenStorage.clearTokens() }

        // Send broadcast to notify UI about logout
        val intent = Intent("ACTION_TOKEN_EXPIRED")
        localBroadCastManager.sendBroadcast(intent)
    }
}