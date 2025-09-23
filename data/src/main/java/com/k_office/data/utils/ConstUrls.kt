package com.k_office.data.utils

import com.k_office.data.provider.BaseConfigProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConstUrls @Inject constructor(
    private val baseConfigProvider: BaseConfigProvider,
) {
    val BASE_URL = baseConfigProvider.provideBaseUrl()

    companion object {
        // Notification routes
        private const val NOTIFICATION_ROUTE = "/notification"
        const val SEND_MESSAGING_TOKEN = "$NOTIFICATION_ROUTE/send-messaging-token"

        const val REGISTER_FCM_TOKEN = "/register-fcm-token"

        // Auth routes
        private const val AUTH_ROUTE = "/auth"
        const val SEND_OTP = "$AUTH_ROUTE/send-otp"
        const val VERIFY_OTP = "$AUTH_ROUTE/verify-otp"
        const val VERIFY_REGISTER = "$AUTH_ROUTE/verify-register"
        const val REGISTER = "$AUTH_ROUTE/register"
        const val REFRESH = "$AUTH_ROUTE/refresh"
        const val LOGOUT = "$AUTH_ROUTE/logout"

        // User routes
        private const val USER_ROUTE = "/user"
        const val REFRESH_USER_INFO = "$USER_ROUTE/refresh"

        const val UPDATE_USER_INFO = "$USER_ROUTE/update"
    }
}