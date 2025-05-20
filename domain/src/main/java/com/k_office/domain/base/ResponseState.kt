package com.k_office.domain.base

sealed class ResponseState<out T>(val data: T? = null, val throwable: Throwable? = null) {
    class Success<out T>(data: T): ResponseState<T>(data)
    class Error<out T>(throwable: Throwable): ResponseState<T>(throwable = throwable)
}