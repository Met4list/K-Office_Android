package com.k_office.data.utils

import android.annotation.SuppressLint
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
import timber.log.Timber
import javax.inject.Inject

class TokenRefreshInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val authApiService: AuthApiService,
    private val localBroadCastManager: LocalBroadcastManager
) : Interceptor {

    private val mutex = Mutex()

    @SuppressLint("TimberArgCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        // If response is 401 and it's not an auth endpoint, try to refresh token
        if (response.code == 401) {
            // Skip if this is already a retried request post-refresh to avoid loops
            if (originalRequest.header("X-Retry-After-Refresh") == "true") {
                return response
            }

            // Attempt refresh if we have a refresh token available, even when the
            // original request had no Authorization header (e.g., token was missing/expired)
            Timber.d("Received 401 for %s, attempting refresh (if refresh token exists)", originalRequest.url.encodedPath)
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

            Timber.d("Attempting to refresh token with refresh token: %s...", refreshToken.take(10))
            val response = authApiService.refreshToken(RefreshTokenRequest(refreshToken))

            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val authTokens = AuthTokens(dto.accessToken, dto.refreshToken, dto.expiresIn)
                    val expiryTime = System.currentTimeMillis() + (dto.expiresIn * 60 * 1000)
                    tokenStorage.saveTokens(dto.accessToken, dto.refreshToken, expiryTime)
                    Timber.d("Token refresh successful, new access token: %s...", dto.accessToken.take(10))
                    Result.success(authTokens)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Timber.e("Token refresh failed: %d, body: %s", response.code(), response.errorBody()?.string())
                Result.failure(Exception("Token refresh failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during token refresh: %s", e.message)
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
            .header("X-Retry-After-Refresh", "true")
            .build()

        return chain.proceed(newRequest)
    }

    private fun handleRefreshFailure() {
        runBlocking { tokenStorage.clearTokens() }

        val intent = Intent("ACTION_TOKEN_EXPIRED")
        localBroadCastManager.sendBroadcast(intent)
    }
}