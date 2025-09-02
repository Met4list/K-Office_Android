package com.k_office.domain.use_case

import com.k_office.data.storage.CurrentUserStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.toUIText
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.CurrentUserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val currentUserStorage: CurrentUserStorage,
) : BaseUseCase<Unit, Flow<DataState<CurrentUserModel>>> {
    override suspend fun invoke(request: Unit): Flow<DataState<CurrentUserModel>> =
        flow {
            try {
                emit(DataState.Loading)
                val response = currentUserStorage.getUser()
                val mappedModel = response?.let { CurrentUserMapper.mapTo(it) }
                emit(DataState.Success(mappedModel))
                emit(DataState.Default)
            } catch (t: Throwable) {
                emit(DataState.Failure(t.toUIText()))
            }
        }
}