package com.example.chatter.features.calls.data.utils

import io.getstream.video.android.core.Call

suspend fun endCall( call : Call){
        try {
            println("🎤 HARD ending AUDIO call: ${call.id}")
            call.end()
        } catch (e: Exception) {
            println("⚠️ Failed to cleanup call: ${e.message}")
        }
}