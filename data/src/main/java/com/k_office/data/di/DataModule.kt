package com.k_office.data.di

import android.content.Context
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_office.data.api.AuthApiService
import com.k_office.data.api.KOfficeApiService
import com.k_office.data.api.NotificationApiService
import com.k_office.data.api.UserApiService
import com.k_office.data.provider.BaseConfigProvider
import com.k_office.data.storage.CurrentUserStorage
import com.k_office.data.storage.CurrentUserStorageImpl
import com.k_office.data.storage.TokenStorage
import com.k_office.data.storage.TokenStorageImpl
import com.k_office.data.utils.AuthInterceptor
import com.k_office.data.utils.ConstUrls
import com.k_office.data.utils.TokenRefreshInterceptor
import com.k_office.data.utils.addDefaultInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideConstUrls(baseConfigProvider: BaseConfigProvider): ConstUrls =
        ConstUrls(baseConfigProvider)

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        constUrls: ConstUrls,
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(constUrls.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    @Named("with_auth")
    fun provideAuthenticatedRetrofit(
        @Named("with_auth") okHttpClient: OkHttpClient,
        gson: Gson,
        constUrls: ConstUrls,
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(constUrls.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    @Named("token_refresh")
    fun provideTokenRefreshRetrofit(
        @Named("token_refresh") okHttpClient: OkHttpClient,
        gson: Gson,
        constUrls: ConstUrls,
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(constUrls.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideKOfficeApiService(retrofit: Retrofit): KOfficeApiService =
        retrofit.create(KOfficeApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideUserApiService(@Named("with_auth") retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideNotificationApiService(@Named("with_auth") retrofit: Retrofit): NotificationApiService =
        retrofit.create(NotificationApiService::class.java)

    @Provides
    @Singleton
    @Named("token_refresh")
    fun provideTokenRefreshUserApiService(@Named("token_refresh") retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        val sharedPrefs = context.getSharedPreferences("tokens_config", Context.MODE_PRIVATE)
        return TokenStorageImpl(sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideCurrentUserStorage(@ApplicationContext context: Context): CurrentUserStorage {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("user_config", Context.MODE_PRIVATE)
        return CurrentUserStorageImpl(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        baseConfigProvider: BaseConfigProvider,
    ): OkHttpClient {
        val chuckerInterceptor = ChuckerInterceptor.Builder(context).build()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addDefaultInterceptor()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)

        if (baseConfigProvider.provideIsDevEnv() || baseConfigProvider.provideIsDebug()) {
            okHttpClient.addInterceptor(chuckerInterceptor)
        }

        return okHttpClient.build()
    }

    @Provides
    @Singleton
    @Named("with_auth")
    fun provideAuthenticatedOkHttpClient(
        @ApplicationContext context: Context,
        baseConfigProvider: BaseConfigProvider,
        tokenStorage: TokenStorage,
        tokenRefreshInterceptor: TokenRefreshInterceptor,
    ): OkHttpClient {
        val chuckerInterceptor = ChuckerInterceptor.Builder(context).build()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AuthInterceptor(tokenStorage))
            .addDefaultInterceptor()
            .addInterceptor(tokenRefreshInterceptor)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)

        if (baseConfigProvider.provideIsDevEnv() || baseConfigProvider.provideIsDebug()) {
            okHttpClient.addInterceptor(chuckerInterceptor)
        }

        return okHttpClient.build()
    }

    @Provides
    @Singleton
    @Named("token_refresh")
    fun provideTokenRefreshOkHttpClient(
        @ApplicationContext context: Context,
        baseConfigProvider: BaseConfigProvider,
        tokenStorage: TokenStorage,
    ): OkHttpClient {
        val chuckerInterceptor = ChuckerInterceptor.Builder(context).build()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AuthInterceptor(tokenStorage))
            .addDefaultInterceptor()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)

        if (baseConfigProvider.provideIsDevEnv() || baseConfigProvider.provideIsDebug()) {
            okHttpClient.addInterceptor(chuckerInterceptor)
        }

        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    @Provides
    @Singleton
    fun provideTokenRefreshInterceptor(
        @ApplicationContext context: Context,
        tokenStorage: TokenStorage,
        authApiService: AuthApiService,
        @Named("token_refresh") userApiService: UserApiService,
    ): TokenRefreshInterceptor = TokenRefreshInterceptor(
        tokenStorage,
        authApiService,
        userApiService,
        localBroadCastManager = LocalBroadcastManager.getInstance(context)
    )
}