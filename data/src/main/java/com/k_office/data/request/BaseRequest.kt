package com.k_office.data.request

import com.google.gson.annotations.SerializedName

sealed class BaseRequest {
    class BalanceRequest(
        @SerializedName("Method")
        private val method: String = BALANCE_BONUS_KEY,
        @SerializedName("Parameters")
        val parameters: Parameters,
    ): BaseRequest()

    class RegistrationRequest(
        @SerializedName("Method")
        private val method: String = REGISTRATION_BONUS_KEY,
        @SerializedName("Data")
        val data: List<Data>
    ): BaseRequest()

    class Authorization(
        @SerializedName("Method")
        private val method: String = AUTORIZATION_BONUS_KEY,
        @SerializedName("Data")
        val data: List<Data>
    ): BaseRequest()

    class Parameters(
        @SerializedName("Telephones")
        val telephones: List<String>
    )

    class Data(
        val url: String = BASE_API_URL,
        @SerializedName("Telephone")
        val telephone: String,
        @SerializedName("Name")
        val name: String,
        @SerializedName("Adress")
        val address: String
    )

    companion object {
        private const val REGISTRATION_BONUS_KEY = "RegistrationBonus"
        private const val BALANCE_BONUS_KEY = "GetBalanceBonus"
        private const val AUTORIZATION_BONUS_KEY = "AuthorizationBonus"
        private const val BASE_API_URL = "k-office.api"
    }
}