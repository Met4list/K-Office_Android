package com.k_office.domain.use_case

import com.k_office.data.storage.CurrentUserStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.UserDataSource
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.CurrentUserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateUserInfoUseCase @Inject constructor(
    private val userDataSource: UserDataSource,
    private val currentUserStorage: CurrentUserStorage
): BaseUseCase<Unit, Flow<DataState<CurrentUserModel>>> {
    override suspend fun invoke(request: Unit): Flow<DataState<CurrentUserModel>> = flow {
        emit(DataState.Loading)
        try {
            val response = userDataSource.updateUserInfo()
            val mappedModel = CurrentUserMapper.mapTo(response)
            currentUserStorage.insertUser(response)
            emit(DataState.Success(data = mappedModel))
            emit(DataState.Default)
        } catch (t: Throwable) {
            // Let the TokenRefreshInterceptor handle token refresh failures
            // It will send ACTION_TOKEN_EXPIRED broadcast which triggers logout
            emit(DataState.Failure(t.toUIText()))
            emit(DataState.Default)
        }
    }
}