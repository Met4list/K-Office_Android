package com.k_office.domain.use_case

import com.k_office.data.storage.CurrentUserStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.UIText
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.UserDataSource
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.UpdateUserData
import com.k_office.domain.model.UpdatedUserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EditCurrentUserUseCase @Inject constructor(
    private val currentUserStorage: CurrentUserStorage,
    private val userDataSource: UserDataSource,
) : BaseUseCase<UpdateUserData, Flow<DataState<UpdatedUserModel>>> {
    override suspend fun invoke(request: UpdateUserData): Flow<DataState<UpdatedUserModel>> =
        flow {
            emit(DataState.Loading)
            try {

                val response = userDataSource.updateUserInfo(request)

                if (response?.userModel != null) {
                    val mappedResponse = CurrentUserMapper.mapTo(response.userModel)
                    currentUserStorage.insertUser(mappedResponse)
                    emit(DataState.Success(data = response))
                } else {
                    emit(DataState.Failure(UIText.getDefaultErrorMessage()))
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                emit(DataState.Failure(t.toUIText()))
            } finally {
                emit(DataState.Default)
            }
        }
}