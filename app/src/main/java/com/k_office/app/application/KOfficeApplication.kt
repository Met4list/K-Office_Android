package com.k_office.app.application

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.k_office.domain.data_source.CurrentUserInfoDataSource
import com.k_office.domain.data_source.KOfficeDataSource
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class KOfficeApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var getCurrentUserInfoDataSource: CurrentUserInfoDataSource

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree(), logCrashlytics())

        // TODO replaced with real userId
        val bonusCard = getCurrentUserInfoDataSource.getBonusCard()
        if (!bonusCard.isNullOrBlank()) {
            FirebaseCrashlytics.getInstance().setUserId(bonusCard)
        }
    }

    private fun logCrashlytics(): Timber.Tree {
        val crashlytics = FirebaseCrashlytics.getInstance()
        return object : Timber.Tree() {
            override fun log(
                priority: Int,
                tag: String?,
                message: String,
                t: Throwable?
            ) {
                if (t != null) {
                    crashlytics.recordException(t)
                }
            }
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024)
                    .build()
            }
            .respectCacheHeaders(false)
            .logger(DebugLogger())
            .build()
    }
}