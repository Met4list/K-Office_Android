package com.k_office.domain.base

sealed class DataState<out T : Any?> {

    object Loading : DataState<Nothing>()

    data class Success<out T : Any>(val data: T?, val info: UIText? = null) : DataState<T>()

    data class Failure(val errorInfo: UIText) : DataState<Nothing>() {
        constructor(info: String) : this(UIText.DynamicString(info))
        constructor() : this(UIText.Companion.getDefaultErrorMessage())
    }

    object Default : DataState<Nothing>()
}