package com.k_office.domain.data_source

import com.k_office.data.api.UserApiService
import com.k_office.data.model.UserRefreshModel
import com.k_office.data.request.UpdateUserDataRequest
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.CurrentUserModel
import com.k_office.domain.model.UpdateUserData
import com.k_office.domain.model.UpdatedUserModel
import javax.inject.Inject

interface UserDataSource {

    suspend fun refreshUserInfo(): UserRefreshModel

    suspend fun updateUserInfo(request: UpdateUserData): UpdatedUserModel

    class Base @Inject constructor(private val apiService: UserApiService) : UserDataSource {

        override suspend fun refreshUserInfo(): UserRefreshModel {
            val response = apiService.refreshUserInfo()
            return CurrentUserMapper.mapTo(response)
        }

        override suspend fun updateUserInfo(request: UpdateUserData): UpdatedUserModel {
            val response = apiService.updateUserInfo(
                UpdateUserDataRequest(
                    request.name,
                    request.phoneNumber
                )
            )

            return response.let { body ->
                UpdatedUserModel(
                    message = body.message,
                    CurrentUserModel(
                        null,
                        bonusCard = body.userModel?.bonusCard,
                        code = body.userModel?.code,
                        createdAt = body.userModel?.createdAt!!,
                        id = body.userModel?.id!!,
                        name = body.userModel?.name!!,
                        telephone = body.userModel?.telephone!!,
                        sum = body.userModel?.sum
                    )
                )
            }
        }
    }
}