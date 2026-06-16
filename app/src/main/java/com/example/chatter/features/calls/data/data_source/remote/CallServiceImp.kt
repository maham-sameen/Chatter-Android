package com.example.chatter.features.calls.data.data_source.remote

import android.content.Context
import androidx.core.content.ContextCompat.startActivity
import io.getstream.video.android.compose.ui.ComposeStreamCallActivity
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.RingingState
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.notifications.NotificationHandler
import io.getstream.video.android.model.StreamCallId
import io.getstream.video.android.ui.common.StreamCallActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.apache.commons.codec.digest.DigestUtils

class CallServiceImp : CallService {
    override fun startOutgoingCallActivity(
        callees: List<String>,
        isVideoCall: Boolean,
        context: Context
    ) {
        val intent = StreamCallActivity.callIntent(
            context = context,
            cid = StreamCallId(type = "default", id = generateRoomId(callees)),
            members = callees.toList(),
            leaveWhenLastInCall = true,
            action = NotificationHandler.ACTION_OUTGOING_CALL,
            clazz = ComposeStreamCallActivity::class.java,
        )
        context.startActivity(intent)
    }


    fun generateRoomId(
        phones : List<String>
    ): String {
        println("\uD83E\uDEE0 [ChatRoomController] generateRoomId ${phones.toString()}")
        val normalized = phones.map { it.trim() }
            .sorted() // 👈 ensures same order always
            .joinToString(separator = "_")
        println("\uD83E\uDEE0 [ChatRoomController] generateRoomId ${normalized.toString()}")
        return DigestUtils.sha256Hex(normalized)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeIncomingCall(
        scope: CoroutineScope,
        context: Context
    ) {
        scope.launch{
            StreamVideo.instanceState.flatMapLatest { instance ->
                instance?.state?.ringingCall ?: flowOf(null)
            }.collectLatest { call ->
                if (call != null) {
                    scope.launch {
                        // Monitor the ringingState on a non-null call
                        call.state.ringingState.collectLatest {
                            if (it is RingingState.Incoming) {
                                startIncomingCallActivity(call , context)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startIncomingCallActivity(call: Call , context: Context) {
        val intent = StreamCallActivity.callIntent(
            context = context,
            cid = StreamCallId.fromCallCid(call.cid),
            leaveWhenLastInCall = true,
            action = NotificationHandler.ACTION_INCOMING_CALL,
            clazz = ComposeStreamCallActivity::class.java,
        )
        context.startActivity(intent)
    }
}