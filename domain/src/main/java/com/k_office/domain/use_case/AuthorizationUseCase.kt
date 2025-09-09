package com.k_office.domain.use_case

import android.content.Context
import com.k_office.data.api.NotificationApiService
import com.k_office.data.provider.BaseConfigProvider
import com.k_office.data.request.RegisterTokenRequest
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.extractServerErrorMessage
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.model.MessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val context: Context,
    private val authDataSource: AuthDataSource,
    private val receiveFCMTokenUseCase: ReceiveFCMTokenUseCase,
    private val notificationApiService: NotificationApiService,
    private val baseConfigProvider: BaseConfigProvider,
) : BaseUseCase<String, Flow<DataState<MessageModel>>> {
    override suspend fun invoke(telephone: String): Flow<DataState<MessageModel>> =
        channelFlow {
            try {
                send(DataState.Loading)
                if (telephone.isNullOrBlank()) {
                    send(DataState.Failure())
                    return@channelFlow
                }
                val fcmTokenResponse = receiveFCMTokenUseCase()
                if (baseConfigProvider.provideIsDevEnv()) notificationApiService.registerFcmToken(
                    RegisterTokenRequest(telephone.drop(1), fcmTokenResponse)
                )
                val response = async(Dispatchers.IO) {
                    authDataSource.sendOtp(
                        telephone,
                        fcmTokenResponse,
                        baseConfigProvider.provideAppSignature(context)!!
                    )
                }.await()
                send(DataState.Success(data = response))

                send(DataState.Default)
            } catch (t: HttpException) {
                send(DataState.Failure(t.extractServerErrorMessage()))
            }
        }
}