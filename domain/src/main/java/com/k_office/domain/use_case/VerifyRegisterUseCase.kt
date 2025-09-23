package com.k_office.domain.use_case

import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.model.MessageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VerifyRegisterUseCase @Inject constructor(
    private val authDataSource: AuthDataSource
) : BaseUseCase<Pair<String, String>, Flow<DataState<MessageModel>>> {
    override suspend fun invoke(request: Pair<String, String>): Flow<DataState<MessageModel>> = flow {
        emit(DataState.Loading)
        try {
            val phoneNumber = request.first
            val otp = request.second
            val response = authDataSource.verifyRegister(phoneNumber, otp)
            emit(DataState.Success(response))
        } catch (t: Throwable) {
            emit(DataState.Failure(t.toUIText()))
        } finally {
            emit(DataState.Default)
        }
    }
}