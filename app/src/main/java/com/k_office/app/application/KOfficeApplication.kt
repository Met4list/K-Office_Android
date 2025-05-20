package com.k_office.app.application

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KOfficeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree(), logCrashlytics())
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
}