package com.k_office.presentation.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.k_office.domain.base.DataState
import com.k_office.domain.use_case.UpdateUserInfoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class UserDataUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Timber.d("UserDataUpdateWorker: Starting user data update")
            
            updateUserInfoUseCase.invoke(Unit).collect { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        Timber.d("UserDataUpdateWorker: Updating user data...")
                    }
                    is DataState.Success -> {
                        Timber.d("UserDataUpdateWorker: User data updated successfully")
                    }
                    is DataState.Failure -> {
                        Timber.e("UserDataUpdateWorker: Failed to update user data: ${dataState.errorInfo}")
                    }
                    else -> Unit
                }
            }
            
            Timber.d("UserDataUpdateWorker: Work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e("UserDataUpdateWorker: Error during user data update: ${e.message}")
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "user_data_update_work"
    }
}
