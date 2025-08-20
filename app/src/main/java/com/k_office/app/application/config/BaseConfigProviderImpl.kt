package com.k_office.app.application.config

import com.k_office.app.BuildConfig
import com.k_office.data.provider.BaseConfigProvider
import javax.inject.Inject

class BaseConfigProviderImpl @Inject constructor(): BaseConfigProvider {
    override fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    override fun provideIsDevEnv(): Boolean {
        return BuildConfig.IS_DEV
    }

    override fun provideIsDebug(): Boolean {
        return BuildConfig.DEBUG
    }
}