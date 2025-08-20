package com.k_office.app.application.di

import com.k_office.app.application.config.BaseConfigProviderImpl
import com.k_office.data.provider.BaseConfigProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun provideBaseConfigProvider(
        baseConfigProviderImpl: BaseConfigProviderImpl,
    ): BaseConfigProvider
}