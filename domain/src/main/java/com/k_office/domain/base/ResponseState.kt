package com.k_office.domain.base

sealed class ResponseState<out T>() {
    data class Success<out T>(val data: T): ResponseState<T>()
    object Loading: ResponseState<Nothing>()
    data class Error(val throwable: Throwable?): ResponseState<Nothing>()
}