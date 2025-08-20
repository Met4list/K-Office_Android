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
    fun provideConstUrls(baseConfigProvider: BaseConfigProvider): ConstUrls
        = ConstUrls(baseConfigProvider)

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson, constUrls: ConstUrls): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(constUrls.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(@Named("auth") okHttpClient: OkHttpClient, gson: Gson, constUrls: ConstUrls): Retrofit =
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
    fun provideAuthApiService(@Named("auth") retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideNotificationApiService(retrofit: Retrofit): NotificationApiService =
        retrofit.create(NotificationApiService::class.java)

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

    // Main OkHttpClient with all interceptors including TokenRefreshInterceptor
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        tokenStorage: TokenStorage,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AuthInterceptor(tokenStorage))
            .addDefaultInterceptor()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .build()

    // Separate OkHttpClient for AuthApiService without TokenRefreshInterceptor
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthOkHttpClient(
        @ApplicationContext context: Context,
        tokenStorage: TokenStorage,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AuthInterceptor(tokenStorage))
            // NOTE: No TokenRefreshInterceptor here to break circular dependency
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
}