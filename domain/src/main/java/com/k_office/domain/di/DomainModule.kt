package com.k_office.domain.di

import android.content.Context
import com.k_office.data.api.KOfficeApi
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.data_source.KOfficeDataSource
import com.k_office.domain.use_case.GetAdsBannersUseCase
import com.k_office.domain.use_case.GetCurrentUserInfoUseCase
import com.k_office.domain.use_case.GetShopsInfoUseCase
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
    fun provideGetCurrentUserInfoUseCase(
        kOfficeApi: KOfficeApi,
        currentUserInfoDataSource: CurrentUserInfoDataSource
    ): GetCurrentUserInfoUseCase =
        GetCurrentUserInfoUseCase(kOfficeApi, currentUserInfoDataSource)

    @Provides
    @Singleton
    fun provideCurrentUserInfoDataSource(@ApplicationContext context: Context): CurrentUserInfoDataSource
        = CurrentUserInfoDataSource.Base(context)

    @Provides
    @Singleton
    fun provideKOfficeDataSource(kOfficeApi: KOfficeApi): KOfficeDataSource =
        KOfficeDataSource.Base(kOfficeApi)

    @Provides
    @Singleton
    fun provideGetShopsInfoUseCase(): GetShopsInfoUseCase =
        GetShopsInfoUseCase()

    @Provides
    @Singleton
    fun provideGetAdsBannersUseCase(): GetAdsBannersUseCase =
        GetAdsBannersUseCase()
}