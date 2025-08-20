package com.k_office.data.api

import com.k_office.data.request.RefreshTokenRequest
import com.k_office.data.response.RefreshTokenResponse
import com.k_office.data.utils.ConstUrls
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST(ConstUrls.REFRESH)
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
}