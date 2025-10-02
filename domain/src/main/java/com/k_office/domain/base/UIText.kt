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