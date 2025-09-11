package com.k_office.data.api

import com.k_office.data.response.UserRefreshResponse
import com.k_office.data.utils.ConstUrls
import retrofit2.http.POST

interface UserApiService {

    @POST(ConstUrls.REFRESH_USER_INFO)
    suspend fun refreshUserInfo(): UserRefreshResponse
}