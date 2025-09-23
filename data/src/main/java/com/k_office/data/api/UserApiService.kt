package com.k_office.data.api

import com.k_office.data.request.UpdateUserDataRequest
import com.k_office.data.response.UpdatedUserResponse
import com.k_office.data.response.UserRefreshResponse
import com.k_office.data.response.UserResponse
import com.k_office.data.utils.ConstUrls
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApiService {

    @GET(ConstUrls.REFRESH_USER_INFO)
    suspend fun refreshUserInfo(): UserRefreshResponse

    @PATCH(ConstUrls.UPDATE_USER_INFO)
    suspend fun updateUserInfo(@Body request: UpdateUserDataRequest): UpdatedUserResponse
}