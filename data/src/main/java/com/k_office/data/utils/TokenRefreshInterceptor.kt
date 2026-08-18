package com.k_office.data.utils

import android.annotation.SuppressLint
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.k_office.data.api.AuthApiService
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

        if (originalRequest.url.encodedPath.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        // Тихо оновлюємо access-токен до запиту, якщо він уже згорів або ось-ось згорить
        val requestToSend = runBlocking {
            mutex.withLock {
                if (tokenStorage.isTokenExpired() || tokenStorage.isAccessTokenExpiringSoon()) {
                    refreshTokenInternal().getOrNull()?.let { newAccessToken ->
                        originalRequest.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    } ?: originalRequest
                } else {
                    originalRequest
                }
            }
        }

        val response = chain.proceed(requestToSend)
        if (response.code == 401) {
            if (originalRequest.header("X-Retry-After-Refresh") == "true") {
                return response
            }
            Timber.d("Received 401 for %s, attempting token refresh", originalRequest.url.encodedPath)
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
                val currentToken = tokenStorage.getAccessToken()
                val originalToken = originalRequest.header("Authorization")?.removePrefix("Bearer ")

                if (currentToken != null && currentToken != originalToken) {
                    return@withLock retryRequestWithNewToken(chain, originalRequest, currentToken)
                }

                val newAccessToken = refreshTokenInternal().getOrNull()
                if (newAccessToken != null) {
                    retryRequestWithNewToken(chain, originalRequest, newAccessToken)
                } else {
                    chain.proceed(originalRequest)
                }
            }
        }
    }

    private suspend fun refreshTokenInternal(): Result<String> {
        return try {
            // CookieJar сам додає refreshToken з cookie на /auth/refresh
            val authResponse = authApiService.refreshToken()
            if (authResponse.isSuccessful) {
                authResponse.body()?.let { dto ->
                    tokenStorage.saveTokens(dto.accessToken, dto.expiresIn, dto.refreshToken)
                    Timber.d("Access token refreshed silently via cookie /auth/refresh")
                    return Result.success(dto.accessToken)
                }
            }
            Timber.e("Auth refresh failed with code: %s", authResponse.code())
            if (authResponse.code() == 401 || authResponse.code() == 403) {
                notifySessionExpired()
            }
            Result.failure(Exception("Failed to refresh token, HTTP ${authResponse.code()}"))
        } catch (e: Exception) {
            Timber.e(e, "Exception during token refresh: %s", e.message)
            Result.failure(e)
        }
    }

    private fun notifySessionExpired() {
        localBroadCastManager.sendBroadcast(Intent("ACTION_TOKEN_EXPIRED"))
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
}
