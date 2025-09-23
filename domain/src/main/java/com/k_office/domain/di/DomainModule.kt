package com.k_office.domain.di

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.k_office.data.api.KOfficeApiService
import com.k_office.data.api.NotificationApiService
import com.k_office.data.api.UserApiService
import com.k_office.data.provider.BaseConfigProvider
import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.TokenStorage
import com.k_office.domain.data_source.AuthDataSource
import com.k_office.domain.data_source.TokenDataSource
import com.k_office.domain.data_source.UserDataSource
import com.k_office.domain.use_case.AuthorizationUseCase
import com.k_office.domain.use_case.EditCurrentUserUseCase
import com.k_office.domain.use_case.GetAdsBannersUseCase
import com.k_office.domain.use_case.GetCurrentUserUseCase
import com.k_office.domain.use_case.GetShopsInfoUseCase
import com.k_office.domain.use_case.LogoutUseCase
import com.k_office.domain.use_case.ReceiveFCMTokenUseCase
import com.k_office.domain.use_case.UpdateUserInfoUseCase
import com.k_office.domain.use_case.VerifyOtpUseCase
import com.k_office.domain.use_case.VerifyRegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(
        authDataSource: AuthDataSource,
        currentUserStorage: CurrentUserStorage,
        tokenStorage: TokenStorage,
    ): VerifyOtpUseCase =
        VerifyOtpUseCase(authDataSource, currentUserStorage, tokenStorage)

    @Provides
    @Singleton
    fun provideAuthDataSource(kOfficeApi: KOfficeApiService): AuthDataSource =
        AuthDataSource.Base(kOfficeApi)

    @Provides
    @Singleton
    fun provideTokenDataSource(tokenStorage: TokenStorage): TokenDataSource {
        return TokenDataSource.Base(tokenStorage)
    }

    @Provides
    @Singleton
    fun provideUserDataSource(userApiService: UserApiService): UserDataSource =
        UserDataSource.Base(userApiService)

    @Provides
    @Singleton
    fun provideGetShopsInfoUseCase(): GetShopsInfoUseCase =
        GetShopsInfoUseCase()

    @Provides
    @Singleton
    fun provideGetAdsBannersUseCase(): GetAdsBannersUseCase =
        GetAdsBannersUseCase()

    @Provides
    @Singleton
    fun provideReceiveFCMTokenUseCase(): ReceiveFCMTokenUseCase = ReceiveFCMTokenUseCase()

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(
        currentUserStorage: CurrentUserStorage,
    ): GetCurrentUserUseCase = GetCurrentUserUseCase(currentUserStorage)

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        currentUserStorage: CurrentUserStorage,
        tokenStorage: TokenStorage
    ): LogoutUseCase = LogoutUseCase(
        currentUserStorage,
        tokenStorage,
        FirebaseMessaging.getInstance()
    )

    @Provides
    @Singleton
    fun provideAuthorizationUseCase(
        @ApplicationContext context: Context,
        authDataSource: AuthDataSource,
        receiveFCMTokenUseCase: ReceiveFCMTokenUseCase,
        notificationApiService: NotificationApiService,
        baseConfigProvider: BaseConfigProvider,
    ): AuthorizationUseCase = AuthorizationUseCase(
        context,
        authDataSource,
        receiveFCMTokenUseCase,
        notificationApiService,
        baseConfigProvider
    )

    @Provides
    @Singleton
    fun provideUpdateUserInfoUseCase(
        userDataSource: UserDataSource,
        currentUserStorage: CurrentUserStorage,
        tokenStorage: TokenStorage
    ): UpdateUserInfoUseCase = UpdateUserInfoUseCase(userDataSource, currentUserStorage, tokenStorage)

    @Provides
    @Singleton
    fun provideVerifyRegisterUseCase(
        authDataSource: AuthDataSource
    ): VerifyRegisterUseCase = VerifyRegisterUseCase(authDataSource)

    @Provides
    @Singleton
    fun provideEditCurrentUserUseCase(
        currentUserStorage: CurrentUserStorage,
        userDataSource: UserDataSource
    ): EditCurrentUserUseCase = EditCurrentUserUseCase(currentUserStorage, userDataSource)
}