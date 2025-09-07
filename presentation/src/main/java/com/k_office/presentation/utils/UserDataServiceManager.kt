package com.k_office.presentation.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.k_office.domain.data_source.TokenDataSource
import com.k_office.presentation.worker.UserDataUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataServiceManager @Inject constructor(
    private val tokenDataSource: TokenDataSource
) {

    fun startPeriodicWork(scope: CoroutineScope, context: Context) {
        scope.launch(Dispatchers.IO) {
            val accessToken = tokenDataSource.getAccessToken()
            val hasValidTokens = accessToken != null
            if (hasValidTokens) {
                Timber.d("Starting UserDataUpdateWorker - user is authenticated")
                schedulePeriodicWork(context)
            } else {
                Timber.d("Skipping UserDataUpdateWorker - user is not authenticated")
            }
        }
    }

    fun stopPeriodicWork(context: Context) {
        Timber.d("Stopping UserDataUpdateWorker")
        WorkManager.getInstance(context).cancelUniqueWork(UserDataUpdateWorker.WORK_NAME)
    }

    fun restartPeriodicWork(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val accessToken = tokenDataSource.getAccessToken()
            val hasValidTokens = accessToken != null
            if (hasValidTokens) {
                Timber.d("Restarting UserDataUpdateWorker - user is authenticated")
                stopPeriodicWork(context)
                schedulePeriodicWork(context)
            } else {
                Timber.d("Stopping UserDataUpdateWorker - user is not authenticated")
                stopPeriodicWork(context)
            }
        }
    }

    private fun schedulePeriodicWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<UserDataUpdateWorker>(
            10,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UserDataUpdateWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )

        Timber.d("UserDataUpdateWorker scheduled to run every 5 minutes")
    }
}
