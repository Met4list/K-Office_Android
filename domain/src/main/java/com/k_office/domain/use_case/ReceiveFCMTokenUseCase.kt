package com.k_office.domain.use_case

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ReceiveFCMTokenUseCase @Inject constructor() {
    suspend operator fun invoke(): String = suspendCancellableCoroutine { continuation ->
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                task.exception?.let { continuation.resumeWithException(it) }
                return@addOnCompleteListener
            }

            continuation.resume(task.result)
            task.addOnCanceledListener {
                continuation.cancel()
            }
        }
    }
}