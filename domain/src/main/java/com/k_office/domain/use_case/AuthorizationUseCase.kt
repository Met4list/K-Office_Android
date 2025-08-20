package com.k_office.domain.use_case

import com.k_office.data.api.NotificationApiService
import com.k_office.data.provider.BaseConfigProvider
import com.k_office.data.request.RegisterTokenRequest
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.UIText
import com.k_office.domain.base.toUIText
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.model.MessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val receiveFCMTokenUseCase: ReceiveFCMTokenUseCase,
    private val notificationApiService: NotificationApiService,
    private val baseConfigProvider: BaseConfigProvider
) : BaseUseCase<String, Flow<DataState<MessageModel>>> {
    override suspend fun invoke(telephone: String): Flow<DataState<MessageModel>> =
        flow {
            try {
                emit(DataState.Loading)
                if (telephone.isNullOrBlank()) {
                    emit(DataState.Failure())
                    return@flow
                }
                coroutineScope {
                    val formatedPhone = telephone.drop(1)
                    val fcmTokenResponse = receiveFCMTokenUseCase()
                    if (baseConfigProvider.provideIsDevEnv()) notificationApiService.registerFcmToken(RegisterTokenRequest(formatedPhone, fcmTokenResponse))
                    val response = async(Dispatchers.IO) { authDataSource.sendOtp(formatedPhone, fcmTokenResponse) }.await()
                    emit(DataState.Success(data = response))
                }
                emit(DataState.Default)
            } catch (t: Throwable) {
                emit(DataState.Failure(t.toUIText()))
            }
        }
}