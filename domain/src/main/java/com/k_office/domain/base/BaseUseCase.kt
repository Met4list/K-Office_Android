package com.k_office.domain.base

interface BaseUseCase<I, O> {

    suspend fun invoke(request: I): O
}