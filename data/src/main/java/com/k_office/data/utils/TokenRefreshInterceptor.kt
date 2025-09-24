package com.k_office.data.utils

import android.annotation.SuppressLint
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.k_office.data.api.AuthApiService
import com.k_office.data.api.UserApiService
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
    private val userApiService: UserApiService,
    private val localBroadCastManager: LocalBroadcastManager
) : Interceptor {

    private val mutex = Mutex()

    @SuppressLint("TimberArgCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 401) {
            if (originalRequest.header("X-Retry-After-Refresh") == "true") {
                return response
            }

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
                val currentToken = tokenStorage.getAccessToken()
                val originalToken = originalRequest.header("Authorization")?.removePrefix("Bearer ")

                if (currentToken != null && currentToken != originalToken) {
                    return@withLock retryRequestWithNewToken(chain, originalRequest, currentToken)
                }

                val refreshResult = refreshTokenInternal()
                when {
                    refreshResult.isSuccess -> {
                        val newAccessToken = refreshResult.getOrNull()?.first
                        if (newAccessToken != null) {
                            retryRequestWithNewToken(chain, originalRequest, newAccessToken)
                        } else {
                            chain.proceed(originalRequest)
                        }
                    }
                    else -> {
                        chain.proceed(originalRequest)
                    }
                }
            }
        }
    }

    private suspend fun refreshTokenInternal(): Result<Pair<String, Long>> {
        return try {
        val authResponse = authApiService.refreshToken()

        if (authResponse.isSuccessful) {
            authResponse.body()?.let { dto ->
                val expiryTime = System.currentTimeMillis() + (dto.expiresIn * 60 * 1000)
                tokenStorage.saveTokens(dto.accessToken, expiryTime)
                Timber.d("Token refresh successful via /auth/refresh, new access token: %s...", dto.accessToken.take(10))
                return Result.success(Pair(dto.accessToken, dto.expiresIn))
            }
        }

        Timber.e("Auth refresh failed with code: %s. Session is lost.", authResponse.code())
        return Result.failure(Exception("Failed to refresh token, HTTP ${authResponse.code()}"))

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
}