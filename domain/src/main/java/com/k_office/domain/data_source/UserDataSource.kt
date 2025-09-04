package com.k_office.domain.data_source

import com.k_office.data.api.UserApiService
import com.k_office.data.model.UserModel
import com.k_office.data.model.UserRefreshModel
import com.k_office.domain.mapper.CurrentUserMapper
import javax.inject.Inject

interface UserDataSource {

    suspend fun updateUserInfo(): UserRefreshModel

    class Base @Inject constructor(private val apiService: UserApiService) : UserDataSource {

        override suspend fun updateUserInfo(): UserRefreshModel {
            val response = apiService.refreshUserInfo()
            return CurrentUserMapper.mapTo(response)
        }
    }
}