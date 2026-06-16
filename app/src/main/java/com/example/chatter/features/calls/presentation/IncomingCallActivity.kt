package com.example.chatter.features.calls.presentation


import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.features.calls.data.utils.ChatterVideoInitHelper
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.ComposeStreamCallActivity
import io.getstream.video.android.compose.ui.components.audio.AudioRoomContent
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
import io.getstream.video.android.model.StreamCallId
import io.getstream.video.android.model.streamCallId
import io.getstream.video.android.ui.common.StreamCallActivity
import io.getstream.video.android.ui.common.util.StreamCallActivityDelicateApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import kotlin.getValue



class  IncomingCallActivity : ComposeStreamCallActivity() {

    private var call : Call? = null

    private val streamVideo : StreamVideo  = StreamVideo.instance()

    private val chatterVideoInitHelper : ChatterVideoInitHelper by inject()

    val onCallAction: (CallAction) -> Unit = { callAction ->
        when (callAction) {
            is ToggleCamera -> call?.camera?.setEnabled(callAction.isEnabled)
            is ToggleMicrophone -> call?.microphone?.setEnabled(callAction.isEnabled)
            is ToggleSpeakerphone -> call?.speaker?.setEnabled(callAction.isEnabled)
            is LeaveCall -> {
                println("🚀 [IncomingCallActivity] onCallAction LeaveCall")
                lifecycleScope.launch {
                    call?.end()
                    finish()
                }

            }
            else -> Unit
        }
    }

    @StreamCallActivityDelicateApi
    override fun onPreCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        println("🚀 [IncomingCallActivity] onPreCreate() called — checking SDK installation")

        runBlocking {
            if (!StreamVideo.isInstalled) {
                println("❗ [IncomingCallActivity] StreamVideo SDK NOT installed — reloading...")
                StreamUserDataStore.instance().data.firstOrNull()?.let {
                    println("🔁 [IncomingCallActivity] Reloading SDK using user data: $it")
                    chatterVideoInitHelper.reloadSdk(
                        data = it,
                    )
                }
            } else {
                println("✔️ [IncomingCallActivity] StreamVideo SDK already installed — no reload needed")
            }
        }

        super.onPreCreate(savedInstanceState, persistentState)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val callCid = intent.streamCallId(NotificationHandler.INTENT_EXTRA_CALL_CID)
        println("📞 [IncomingCallActivity] started with cid: $callCid")
        val isVideoCall = intent.getBooleanExtra("IS_VIDEO_CALL" , false)
        println("📞 [IncomingCallActivity] isVideoCall : ${isVideoCall.toString()}")
        if (callCid == null) {
            println("❌ No call CID found, finishing activity...")
            finish()
            return
        }

        // Get stream client call instance
        call = streamVideo.call(callCid.type, callCid.id)

        setContent {
            if(call != null){
                val context = LocalContext.current
                val activity = context as? Activity
                VideoTheme {
                    val scope = rememberCoroutineScope()
                    RingingCallContent(
                        modifier = Modifier,
                        call = call!!,
                        onBackPressed = {
                            println("📞 IncomingCallActivity onBackPressed")
                            scope.launch{
                                call?.reject()
                                call?.end()
                            }
                            finish() },
                        onCallAction = onCallAction ,
                        isShowingHeader = true ,
                        onAcceptedContent = {
                            // step5 - render the pre-built call content.
                            if(call != null){
                                println("📞 IncomingCallActivity onAcceptedContent")
                                val intent = StreamCallActivity.callIntent(
                                    context = context,
                                    cid = StreamCallId.fromCallCid(call!!.cid),
                                    leaveWhenLastInCall = true,
                                    action = NotificationHandler.ACTION_ACCEPT_CALL,
                                    clazz = AcceptCallActivity::class.java,
                                )

                                println("📞 IncomingCallActivity Starting AcceptCallActivity...")
                                context.startActivity(intent)
                            }
                        }
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