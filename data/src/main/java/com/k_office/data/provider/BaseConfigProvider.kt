package com.k_office.data.provider

import android.content.Context

interface BaseConfigProvider {

    fun provideBaseUrl(): String

    fun provideIsDevEnv(): Boolean

    fun provideIsDebug(): Boolean

    fun provideFlavor(): String

    fun provideBuildTypeName(): String

    fun provideAppSignature(context: Context): String?
}