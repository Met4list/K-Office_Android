package com.k_office.data.api

import com.k_office.data.request.RefreshTokenRequest
import com.k_office.data.request.RegisterUserRequest
import com.k_office.data.request.SendOtpRequest
import com.k_office.data.request.VerifyOtpRequest
import com.k_office.data.response.MessageResponse
import com.k_office.data.response.OtpResponse
import com.k_office.data.utils.ConstUrls
import retrofit2.http.Body
import retrofit2.http.POST

interface KOfficeApiService {

    @POST(ConstUrls.SEND_OTP)
    suspend fun sendOtp(@Body request: SendOtpRequest): MessageResponse

    @POST(ConstUrls.VERIFY_OTP)
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): OtpResponse

    @POST(ConstUrls.REGISTER)
    suspend fun register(@Body request: RegisterUserRequest): OtpResponse

    @POST(ConstUrls.LOGOUT)
    suspend fun logout(@Body request: RefreshTokenRequest): MessageResponse
}