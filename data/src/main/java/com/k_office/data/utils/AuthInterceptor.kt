package com.k_office.data.utils

import android.annotation.SuppressLint
import com.k_office.data.storage.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val tokenStorage: TokenStorage): Interceptor {
    @SuppressLint("TimberArgCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.url.encodedPath.contains("/auth/")) {
            Timber.d("Skipping auth for auth endpoint: %s", originalRequest.url.encodedPath)
            return chain.proceed(originalRequest)
        }

        val accessToken = runBlocking { tokenStorage.getAccessToken() }
        
        Timber.d("Request to: %s, Access token: %s", originalRequest.url.encodedPath, if (accessToken != null) "present" else "null")

        val authenticatedRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            Timber.w("No access token available for request: %s", originalRequest.url.encodedPath)
            originalRequest
        }

        return chain.proceed(authenticatedRequest)
    }
}