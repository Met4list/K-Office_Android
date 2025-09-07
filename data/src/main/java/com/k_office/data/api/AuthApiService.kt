package com.k_office.data.api

import com.k_office.data.response.RefreshTokenResponse
import com.k_office.data.utils.ConstUrls
import retrofit2.Response
import retrofit2.http.GET

interface AuthApiService {

    @GET(ConstUrls.REFRESH)
    suspend fun refreshToken(): Response<RefreshTokenResponse>
}