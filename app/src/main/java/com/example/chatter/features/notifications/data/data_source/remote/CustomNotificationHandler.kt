package com.example.chatter.features.notifications.data.data_source.remote

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.chatter.MainActivity
import com.example.chatter.R
import com.example.chatter.features.calls.presentation.IncomingCallActivity
import io.getstream.video.android.core.notifications.DefaultNotificationHandler
import io.getstream.video.android.core.notifications.NotificationHandler
import io.getstream.video.android.model.StreamCallId


class CustomNotificationHandler(
    private val application: Application,
) : DefaultNotificationHandler(
    application = application,
    hideRingingNotificationInForeground = true,
) {

    // NEW REQUIRED METHOD → This handles ringing notification
    override fun onRingingCall(
        callId: StreamCallId,
        callDisplayName: String,
        payload: Map<String, Any?>
    ) {
        println("🔔 onRingingCall TRIGGERED")
        println("📌 Caller Name: $callDisplayName")
        println("📌 Call ID: ${callId.cid}")

        // Launch your ringing activity
        val intent = Intent(application, IncomingCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(NotificationHandler.INTENT_EXTRA_CALL_CID, callId.cid)
        }

        println("🚀 Launching IncomingCallActivity...")
        application.startActivity(intent)

        // Optionally still show notification (optional)
        super.onRingingCall(callId, callDisplayName, payload)
    }



    override fun onRingingCall(callId: StreamCallId, callDisplayName: String) {
        println("📞 CustomNotificationHandler → onRingingCall triggered")
        println("👉 CallId: $callId")
        println("👉 Caller Name: $callDisplayName")


        super.onRingingCall(callId, callDisplayName)
    }


    @SuppressLint("MissingPermission")
    override fun onMissedCall(callId: StreamCallId, callDisplayName: String) {
        println("❌ CustomNotificationHandler → onMissedCall triggered")
        println("👉 Missed Call ID: $callId")
        println("👉 Caller Name: $callDisplayName")

        val notification = NotificationCompat.Builder(application, getChannelId())
            .setSmallIcon(R.drawable.round_call_missed_24)
            .setContentIntent(buildContentIntent())
            .setContentTitle("Tutorial Missed Call from $callDisplayName")
            .setAutoCancel(true)
            .build()

        println("🔔 Sending missed call notification now...")
        notificationManager.notify(callId.hashCode(), notification)
    }

    private fun buildContentIntent() = PendingIntent.getActivity(
        application,
        0,
        Intent(application, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    ).also {
        println("➡️ buildContentIntent() created → opens MainActivity when tapped.")
    }
}
