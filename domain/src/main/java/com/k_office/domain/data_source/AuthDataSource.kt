package com.k_office.domain.data_source

import com.k_office.data.api.KOfficeApiService
import com.k_office.data.request.SendOtpRequest
import com.k_office.data.request.VerifyOtpRequest
import com.k_office.data.utils.handleResponse
import com.k_office.domain.mapper.OtpMapper
import com.k_office.domain.mapper.RegisterMapper
import com.k_office.domain.model.AuthTypeModel
import com.k_office.domain.model.MessageModel
import com.k_office.domain.model.OtpModel
import com.k_office.domain.model.RegistrationModel
import javax.inject.Inject

interface AuthDataSource {

    suspend fun sendOtp(phoneNumber: String, fcmToken: String, hash: String): AuthTypeModel

    suspend fun verifyOtp(phoneNumber: String, otp: String): OtpModel

    suspend fun verifyRegister(phoneNumber: String, otp: String): MessageModel

    suspend fun register(request: RegistrationModel): OtpModel

    suspend fun logout(): MessageModel

    class Base @Inject constructor(
        private val kOfficeApi: KOfficeApiService
    ) : AuthDataSource {
        override suspend fun sendOtp(
            phoneNumber: String,
            fcmToken: String,
            hash: String,
        ): AuthTypeModel {
            val response = kOfficeApi
                .sendOtp(SendOtpRequest(phoneNumber, fcmToken, hash))
                .handleResponse()
            return AuthTypeModel(phoneNumber, response.type, response.message)
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

        override suspend fun verifyRegister(phoneNumber: String, otp: String): MessageModel {
            val response = kOfficeApi.verifyRegister(VerifyOtpRequest(
                phoneNumber, otp
            )).handleResponse()
            return MessageModel(response.message)
        }

        override suspend fun register(request: RegistrationModel): OtpModel {
            val mappedRequest = RegisterMapper.mapTo(request)
            val response = kOfficeApi.register(mappedRequest)
            return OtpMapper.mapTo(response)
        }

        override suspend fun logout(): MessageModel {
            val response = kOfficeApi.logout()
            return MessageModel(response.message)
        }
    }
}