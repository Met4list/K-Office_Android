package com.k_office.domain.ext

import com.k_office.data.request.RegisterTokenRequest
import com.k_office.domain.base.DataState
import com.k_office.domain.base.Mapper
import com.k_office.domain.base.UIText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

//fun <R, M>executeWithResult(result: (Mapper<T>) -> Unit): Flow<DataState<T>> = flow {
//    try {
//        emit(DataState.Loading)
//
//        emit(DataState.Default)
//    } catch (t: HttpException) {
//        emit(DataState.Failure(UIText.DynamicString(t.localizedMessage)))
//    }
//}