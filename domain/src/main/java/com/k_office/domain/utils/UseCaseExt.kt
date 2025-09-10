package com.k_office.domain.utils

import com.k_office.domain.base.DataState
import com.k_office.domain.base.UIText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import retrofit2.Response

fun <T : Any> Response<T>.executeRequest(): Flow<DataState<T>> {
    return channelFlow {
        send(DataState.Loading)
        try {
            val response = this@executeRequest
            if (response.isSuccessful) {
                val responseBody = async(Dispatchers.IO) { response.body() }.await()
                send(DataState.Success(data = responseBody))
            } else {
                send(DataState.Failure())
            }
            send(DataState.Default)
        } catch (exception: HttpException) {
            send(DataState.Failure(UIText.DynamicString(exception.message.toString())))
        }
    }
}