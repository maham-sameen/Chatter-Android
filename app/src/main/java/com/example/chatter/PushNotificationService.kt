package com.example.chatter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.getstream.android.push.firebase.FirebaseMessagingDelegate
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject

class PushNotificationService : FirebaseMessagingService() {

    private val chatRepo: ChatRepo by inject()

    override fun onNewToken(token: String) {
        println("[PushNotificationService] onNewToken , token : ${token}")
        try {
            FirebaseMessagingDelegate.registerFirebaseToken(token, "chatter_firebase")
        } catch (exception: IllegalStateException) {
            // ChatClient was not initialized
        }
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        println("[PushNotificationService] onMessageReceived , data=${message.data}")
        super.onMessageReceived(message)

        // Let Stream peel off its own messages first
        try {
            if (FirebaseMessagingDelegate.handleRemoteMessage(message)) return
        } catch (_: IllegalStateException) { /* fall through */ }

        val data = message.data
        val roomId = data["room_id"] ?: return
        val lastMessage = data["last_message"] ?: return
        val timeStampRaw = data["time_stamp"] ?: return
        val title = data["title"] ?: "New message"
        val body  = data["body"]  ?: lastMessage
        val timeStamp = runCatching { Json.decodeFromString<Long>(timeStampRaw) }.getOrNull()
            ?: timeStampRaw.toLongOrNull() ?: return

        // 1. Update local DB via your existing worker — this is what bumps the unread count
        chatRepo.uploadDataToWorker(
            roomId = roomId,
            timeStamp = timeStamp,
            lastMessage = lastMessage,
            workManager = WorkManager.getInstance(applicationContext)
        )

        // 2. Build and show the notification yourself
        showChatNotification(roomId, title, body)
    }

    private fun showChatNotification(roomId: String, title: String, body: String) {
        val channelId = "chatter_messages"
        val nm = getSystemService(NotificationManager::class.java)

        // create channel once (idempotent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Messages", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }

        // tap intent — open MainActivity, pass roomId so the app navigates to the conversation
        val tapIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("room_id", roomId)
        }
        val pending = PendingIntent.getActivity(
            this, roomId.hashCode(), tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.darklogo)  // your icon
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // use a stable per-room id so each chat collapses to one notification
        nm.notify(roomId.hashCode(), notification)
    }
}