package com.k_office.presentation.base.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class SmsRetrieverCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    // replay=1: SMS може прийти до екрана OTP — не губимо повідомлення
    private val _messages = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 4)
    val messages = _messages.asSharedFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    if (!message.isNullOrBlank()) {
                        Timber.d("SMS Retriever: повідомлення отримано")
                        _messages.tryEmit(message)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    Timber.w("SMS Retriever: timeout")
                }

                else -> {
                    Timber.w("SMS Retriever: status ${status?.statusCode}")
                }
            }
        }
    }

    @Volatile
    private var registered = false

    // Receiver на Application, потім чекаємо GMS — інакше SMS прилітає в нікуди
    suspend fun startListening() {
        registerReceiver()
        suspendCancellableCoroutine { cont ->
            try {
                SmsRetriever.getClient(context)
                    .startSmsRetriever()
                    .addOnSuccessListener {
                        Timber.d("SMS Retriever запущено")
                        if (cont.isActive) cont.resume(Unit)
                    }
                    .addOnFailureListener { e ->
                        Timber.e(e, "Не вдалося запустити SMS Retriever")
                        if (cont.isActive) cont.resume(Unit)
                    }
            } catch (e: Exception) {
                Timber.e(e, "Виняток при старті SMS Retriever")
                if (cont.isActive) cont.resume(Unit)
            }
        }
    }

    @Synchronized
    private fun registerReceiver() {
        if (registered) return
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION,
            null,
            ContextCompat.RECEIVER_EXPORTED
        )
        registered = true
    }
}
