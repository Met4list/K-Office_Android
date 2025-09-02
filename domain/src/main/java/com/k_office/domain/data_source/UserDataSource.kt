package com.k_office.domain.data_source

import com.k_office.data.api.UserApiService
import com.k_office.data.model.UserModel
import com.k_office.domain.mapper.CurrentUserMapper
import javax.inject.Inject

interface UserDataSource {

    suspend fun updateUserInfo(): UserModel

    class Base @Inject constructor(private val apiService: UserApiService) : UserDataSource {

        override suspend fun updateUserInfo(): UserModel {
            val response = apiService.refreshUserInfo()
            return CurrentUserMapper.mapTo(response)
        }
    }
}