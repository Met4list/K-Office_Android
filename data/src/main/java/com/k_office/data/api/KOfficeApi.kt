package com.k_office.data.api

import com.k_office.data.base.BaseResponse
import com.k_office.data.base.Constants.INSERT_JSON_POST_KEY
import com.k_office.data.base.Constants.JSON_POST_KEY
import com.k_office.data.request.BaseRequest
import com.k_office.data.response.BonusCardResponse
import com.k_office.data.response.CardInformationResponse
import com.k_office.data.response.CurrentUserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface KOfficeApi {

    @POST(JSON_POST_KEY)
    suspend fun getBalanceBonus(@Body request: BaseRequest.BalanceRequest): BaseResponse<CurrentUserInfoResponse>

    @POST(INSERT_JSON_POST_KEY)
    suspend fun registrationBonus(@Body request: BaseRequest.RegistrationRequest): BaseResponse<CardInformationResponse>

    @POST(INSERT_JSON_POST_KEY)
    suspend fun authorizationBonus(@Body request: BaseRequest.Authorization): BaseResponse<BonusCardResponse>
}