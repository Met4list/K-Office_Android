package com.k_office.data.utils

import com.k_office.data.response.MessageResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> Response<T>.handleResponse(): T {
    if (this.isSuccessful) {
        return this.body() ?: throw IllegalStateException("Successful response body was null.")
    } else {
        val errorBodyString = this.errorBody()?.string()
        val errorMessage = try {
            errorBodyString?.let {
                Json.decodeFromString<MessageResponse>(it).message
            } ?: "Unknown error"
        } catch (e: Exception) {
            "Error parsing API response message."
        }

        val httpCode = (this as? HttpException)?.code() ?: -1

        throw IllegalStateException("HTTP Error $httpCode: $errorMessage")
    }
}