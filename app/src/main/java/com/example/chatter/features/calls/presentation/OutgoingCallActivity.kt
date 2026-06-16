package com.example.chatter.features.calls.presentation


import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.chatter.features.calls.data.utils.ChatterVideoInitHelper
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.ComposeStreamCallActivity
import io.getstream.video.android.compose.ui.components.call.ringing.RingingCallContent
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.call.state.CallAction
import io.getstream.video.android.core.call.state.LeaveCall
import io.getstream.video.android.core.call.state.ToggleCamera
import io.getstream.video.android.core.call.state.ToggleMicrophone
import io.getstream.video.android.core.call.state.ToggleSpeakerphone
import io.getstream.video.android.core.notifications.NotificationHandler
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.streamCallId
import io.getstream.video.android.ui.common.util.StreamCallActivityDelicateApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject


class OutgoingCallActivity : ComposeStreamCallActivity() {


    private val chatterVideoInitHelper : ChatterVideoInitHelper by inject()

    private var call : Call? = null

    private val streamVideo : StreamVideo  = StreamVideo.instance()


    val onCallAction: (CallAction) -> Unit = { callAction ->
        when (callAction) {
            is ToggleCamera -> call?.camera?.setEnabled(callAction.isEnabled)
            is ToggleMicrophone -> call?.microphone?.setEnabled(callAction.isEnabled)
            is ToggleSpeakerphone -> call?.speaker?.setEnabled(callAction.isEnabled)
            is LeaveCall -> {
                println("🚀 [OutgoingCallActivity] onCallAction LeaveCall ")
                lifecycleScope.launch{
                    call?.end()
                    finish()
                }
            }
            else -> Unit
        }
    }



    @StreamCallActivityDelicateApi
    override fun onPreCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        println("🚀 [OutgoingCallActivity] onPreCreate() called — checking SDK installation")

        runBlocking {
            if (!StreamVideo.isInstalled) {
                println("❗ [OutgoingCallActivity] StreamVideo SDK NOT installed — reloading...")
                StreamUserDataStore.instance().data.firstOrNull()?.let {
                    println("🔁 [OutgoingCallActivity] Reloading SDK using user data: $it")
                    chatterVideoInitHelper.reloadSdk(
                        data = it,
                    )
                }
            } else {
                println("✔️ [OutgoingCallActivity] StreamVideo SDK already installed — no reload needed")
            }
        }

        super.onPreCreate(savedInstanceState, persistentState)
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val callCid = intent.streamCallId(NotificationHandler.INTENT_EXTRA_CALL_CID)
        println("📞 OutgoingCallActivity started with cid: $callCid")

        if (callCid == null) {
            println("❌ No call CID found, finishing activity...")
            finish()
            return
        }

        // Get stream client call instance
        call = streamVideo.call(callCid.type, callCid.id)

        setContent {
            if(call != null){
                VideoTheme {
                    val scope = rememberCoroutineScope()
                    RingingCallContent(
                        modifier = Modifier,
                        call = call!!,
                        onBackPressed = {
                            println("📞 OutgoingCallActivity onBackPressed")
                            scope.launch{ call?.end() }
                            finish() },
                        onAcceptedContent = {
                            println("📞 OutgoingCallActivity onAcceptedContent")
                        },
                        onCallAction = onCallAction ,
                        isShowingHeader = true
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycleScope.launch {
            call?.end()
        }
        super.onDestroy()
    }
}