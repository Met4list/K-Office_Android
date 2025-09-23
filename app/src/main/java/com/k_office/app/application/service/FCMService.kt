package com.k_office.app.application.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.k_office.app.R
import com.k_office.data.api.NotificationApiService
import com.k_office.data.provider.BaseConfigProvider
import com.k_office.data.request.MessagingTokenRequest
import com.k_office.data.storage.CurrentUserStorage
import com.k_office.presentation.screen.main_activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    private val TAG = this.javaClass.name

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var notificationApiService: NotificationApiService

    @Inject
    lateinit var currentUserStorage: CurrentUserStorage

    @Inject
    lateinit var baseConfigProvider: BaseConfigProvider

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d(TAG, "Message data payload: ${remoteMessage.data}")

            val messageTitle = remoteMessage.data["title"]
            val messageBody = remoteMessage.data["body"]

            // Extract the OTP if needed
            val otp = remoteMessage.data["otp"]

            sendNotification(messageTitle, messageBody)
        }
    }

    override fun onNewToken(token: String) {
        Timber.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        coroutineScope.launch {
            val userId = currentUserStorage.getUserId().first()
            val isLoggedIn = currentUserStorage.isLoggedIn().first()
            token?.let {
                if (userId == null) return@launch
                if (isLoggedIn && baseConfigProvider.provideIsDevEnv()) notificationApiService.sendMessagingToken(
                    MessagingTokenRequest(it, userId)
                )
            }
        }
        Timber.d(TAG, "Sending token to server: $token")
    }

    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId =
            getString(R.string.default_notification_channel_id) // Define this in strings.xml
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.k_office.presentation.R.drawable.ic_app_logo) // Use your own notification icon
            .setContentTitle(messageTitle ?: "FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Random().nextInt(), notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}