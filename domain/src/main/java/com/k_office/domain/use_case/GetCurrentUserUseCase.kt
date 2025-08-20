package com.k_office.domain.use_case

import com.k_office.data.storage.CurrentUserStorage
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.mapper.CurrentUserMapper
import com.k_office.domain.model.CurrentUserModel
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GetCurrentUserUseCase @Inject constructor(
    private val currentUserStorage: CurrentUserStorage,
) : BaseUseCase<Unit, CurrentUserModel?> {
    override suspend fun invoke(request: Unit): CurrentUserModel? =
        suspendCancellableCoroutine { continuation ->
            try {
                val response = currentUserStorage.getUser()
                val mappedModel = response?.let { CurrentUserMapper.mapTo(it) }
                continuation.resume(mappedModel)
            } catch (t: Throwable) {
                continuation.resumeWithException(t)
            }
        }
}