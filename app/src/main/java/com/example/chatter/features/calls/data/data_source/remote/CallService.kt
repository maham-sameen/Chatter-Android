package com.example.chatter.features.calls.data.data_source.remote

import android.content.Context
import kotlinx.coroutines.CoroutineScope

interface CallService {

    fun startOutgoingCallActivity(
        callees: List<String>,
        isVideoCall: Boolean,
        context: Context
    )
    fun observeIncomingCall( scope : CoroutineScope , context: Context)
}