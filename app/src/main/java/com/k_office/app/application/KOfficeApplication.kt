package com.k_office.app.application

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KOfficeApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree(), logCrashlytics())
//        FirebaseCrashlytics.getInstance().setUserId()
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