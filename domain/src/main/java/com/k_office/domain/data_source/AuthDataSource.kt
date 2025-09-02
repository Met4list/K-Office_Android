package com.k_office.domain.data_source

import com.k_office.data.api.KOfficeApiService
import com.k_office.data.request.RefreshTokenRequest
import com.k_office.data.request.SendOtpRequest
import com.k_office.data.request.VerifyOtpRequest
import com.k_office.domain.mapper.OtpMapper
import com.k_office.domain.mapper.RegisterMapper
import com.k_office.domain.model.MessageModel
import com.k_office.domain.model.OtpModel
import com.k_office.domain.model.RegistrationModel
import javax.inject.Inject

interface AuthDataSource {

    suspend fun sendOtp(phoneNumber: String, fcmToken: String, hash: String): MessageModel

    suspend fun verifyOtp(phoneNumber: String, otp: String): OtpModel

    suspend fun register(request: RegistrationModel): OtpModel

    suspend fun logout(refreshToken: String): MessageModel

    class Base @Inject constructor(
        private val kOfficeApi: KOfficeApiService
    ) : AuthDataSource {
        override suspend fun sendOtp(
            phoneNumber: String,
            fcmToken: String,
            hash: String,
        ): MessageModel {
            val response = kOfficeApi.sendOtp(
                    SendOtpRequest(
                        phoneNumber,
                        fcmToken,
                        hash
                    )
                )

            return MessageModel(response?.message!!)
        }

        override suspend fun verifyOtp(
            phoneNumber: String,
            otp: String,
        ): OtpModel {
            val response = kOfficeApi.verifyOtp(
                VerifyOtpRequest(
                    phoneNumber, otp
                )
            )
            val mappedResponse = OtpMapper.mapTo(response)
            return mappedResponse
        }

        override suspend fun register(request: RegistrationModel): OtpModel {
            val mappedRequest = RegisterMapper.mapTo(request)
            val response = kOfficeApi.register(mappedRequest)
            return OtpMapper.mapTo(response)
        }

        override suspend fun logout(refreshToken: String): MessageModel {
            val response = kOfficeApi.logout(RefreshTokenRequest(refreshToken))
            return MessageModel(response.message)
        }
    }
}