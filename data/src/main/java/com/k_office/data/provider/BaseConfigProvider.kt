package com.k_office.data.provider

interface BaseConfigProvider {

    fun provideBaseUrl(): String

    fun provideIsDevEnv(): Boolean

    fun provideIsDebug(): Boolean
}