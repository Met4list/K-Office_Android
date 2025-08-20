package com.k_office.data.api

import com.k_office.data.request.MessagingTokenRequest
import com.k_office.data.request.RegisterTokenRequest
import com.k_office.data.utils.ConstUrls
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApiService {

    @POST(ConstUrls.SEND_MESSAGING_TOKEN)
    suspend fun sendMessagingToken(@Body request: MessagingTokenRequest)

    @POST(ConstUrls.REGISTER_FCM_TOKEN)
    suspend fun registerFcmToken(@Body request: RegisterTokenRequest)
}