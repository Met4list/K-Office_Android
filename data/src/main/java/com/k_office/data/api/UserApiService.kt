package com.k_office.data.api

import com.k_office.data.response.UserResponse
import com.k_office.data.utils.ConstUrls
import retrofit2.http.GET

interface UserApiService {

    @GET(ConstUrls.REFRESH_USER_INFO)
    suspend fun refreshUserInfo(): UserResponse
}