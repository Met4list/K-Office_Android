package com.k_office.domain.base

import android.content.Context
import androidx.annotation.StringRes
import com.google.gson.Gson
import com.k_office.data.R
import com.k_office.data.response.MessageResponse
import retrofit2.HttpException

sealed class UIText {
    data class DynamicString(val value: String) : UIText()
    data class StringResource(@StringRes val id: Int, val args: List<String>? = null) : UIText()

    fun getString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> {
                when(args?.size){
                    1 -> {context.getString(id, args[0])}
                    2 -> {context.getString(id, args[0], args[1])}
                    else -> {context.getString(id)}
                }
            }
        }
    }

    companion object{
        fun getDefaultErrorMessage() = StringResource(R.string.default_error_message)
    }
}

fun Throwable?.toUIText(): UIText =
    this?.message?.let {
        UIText.DynamicString(it)
    } ?: UIText.getDefaultErrorMessage()

fun Throwable.extractServerErrorMessage(): UIText {
    return try {
        if (this is HttpException) {
            val exception = this as HttpException
            val errorBody = exception.response()?.errorBody()?.string()
            if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, MessageResponse::class.java)
                val message = errorResponse.message
                if (!message.isNullOrEmpty()) {
                    UIText.DynamicString(message)
                } else {
                    toUIText()
                }
            } else {
                toUIText()
            }
        } else {
            toUIText()
        }
    } catch (e: Exception) {
        e.toUIText()
    }
}