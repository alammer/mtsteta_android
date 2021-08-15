package ru.mtsteta.flixnet.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.mtsteta.flixnet.MainActivity
import ru.mtsteta.flixnet.R

class MyFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        Log.i("MyFCMservice", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.i(
                "MyFCMservice",
                "Function called: onMessageReceived() message data payload: ${remoteMessage.data}"
            )
        }

        remoteMessage.notification?.let {
            Log.i(
                "MyFCMservice",
                "Function called: onMessageReceived() message Notification Body: ${it.body} "
            )
        }

        remoteMessage.notification?.body?.let { sendNotification(it) }
    }

    override fun onNewToken(token: String) {
        Log.i("MyFCMservice", "Function called: onNewToken() refreshed token: $token ")
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.default_fg_notification_channel_id)
        val channelName = getString(R.string.default_fg_notofication_channel_name)

        createFGNotificationChannel(pendingIntent, channelId, channelName, messageBody)
    }

    private fun createFGNotificationChannel(
        channelIntent: PendingIntent,
        channelId: String,
        channelName: String,
        message: String
    ) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_filled_star)
            .setColor(ContextCompat.getColor(this, R.color.orange_700))
            .setContentTitle(getString(R.string.default_fg_notification_channel_title))
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(channelIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}