package com.example.chatter.features.calls.presentation


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.chatter.MainActivity
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.model.CallEndBus
import com.example.chatter.features.calls.data.utils.ChatterVideoInitHelper
import com.example.chatter.features.calls.data.utils.endCall
import com.example.chatter.features.chats.domain.repository.ChatRepo
import io.getstream.video.android.compose.permission.rememberCallPermissionsState
import io.getstream.video.android.compose.ui.ComposeStreamCallActivity
import io.getstream.video.android.compose.ui.StreamCallActivityComposeDelegate
import io.getstream.video.android.compose.ui.components.call.activecall.AudioOnlyCallContent
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.actions.DefaultOnCallActionHandler
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.call.state.LeaveCall
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.ui.common.StreamActivityUiDelegate
import io.getstream.video.android.ui.common.StreamCallActivity
import io.getstream.video.android.ui.common.StreamCallActivityConfiguration
import io.getstream.video.android.ui.common.util.StreamCallActivityDelicateApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject


class AcceptCallActivity : ComposeStreamCallActivity() {

    override val uiDelegate: StreamActivityUiDelegate<StreamCallActivity> = StreamDemoUiDelegate()

    private val chatterVideoInitHelper : ChatterVideoInitHelper by inject ()

    override fun loadConfigFromIntent(intent: Intent?): StreamCallActivityConfiguration {
        println("📌 [AcceptCallActivity] loadConfigFromIntent() called with intent = $intent")

        return super.loadConfigFromIntent(intent)
            .copy(closeScreenOnCallEnded = false, canSkipPermissionRationale = false)
            .also {
                println("⚙️ [AcceptCallActivity] StreamCallActivityConfiguration applied: $it")
            }
    }

    @StreamCallActivityDelicateApi
    override fun onPreCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        println("🚀 [AcceptCallActivity] onPreCreate() called — checking SDK installation")

        runBlocking {
            if (!StreamVideo.isInstalled) {
                println("❗ [AcceptCallActivity] StreamVideo SDK NOT installed — reloading...")
                StreamUserDataStore.instance().data.firstOrNull()?.let {
                    println("🔁 [AcceptCallActivity] Reloading SDK using user data: $it")
                    chatterVideoInitHelper.reloadSdk(
                        data = it,
                    )
                }
            } else {
                println("✔️ [AcceptCallActivity] StreamVideo SDK already installed — no reload needed")
            }
        }

        super.onPreCreate(savedInstanceState, persistentState)
    }

    private inner class StreamDemoUiDelegate : StreamCallActivityComposeDelegate() {

        @Composable
        override fun StreamCallActivity.LoadingContent(call: Call) {
            println("⏳ [AcceptCallActivity] LoadingContent(), call id = ${call.id}")
            LoadingAnimation()
        }

        @Composable
        override fun StreamCallActivity.CallDisconnectedContent(call: Call) {
            println("📴 [AcceptCallActivity] CallDisconnectedContent triggered for call id = ${call.id}")
            goBackToMainScreen()
        }

        @OptIn(StreamCallActivityDelicateApi::class)
        @Composable
        override fun StreamCallActivity.VideoCallContent(call: Call) {
            println("📴 [AcceptCallActivity] VideoCallContent ")
            val isVideoCall = intent.getBooleanExtra("IS_VIDEO_CALL" , false)
            println("📞 [AcceptCallActivity] isVideoCall : ${isVideoCall.toString()}")
            val context = LocalContext.current
            val permissions = rememberCallPermissionsState(
                call = call,
                permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                onPermissionsResult = { perms ->
                    if (perms.values.contains(false)) {
                        Toast.makeText(context, "Please grant all permissions", Toast.LENGTH_LONG).show()
                    }
                }
            )
            LaunchedEffect(call.id) {
                call.state.endedAt.collect { endedAt ->
                    if (endedAt != null) {
                        println("🛑 Call ended (remote or local)")
                        goBackToMainScreen()
                    }
                }
            }

            CallContent(
                call = call,
                modifier = Modifier.fillMaxSize(),
                permissions = permissions,
                onCallAction = { action ->
                    if(action == LeaveCall) {
                        lifecycleScope.launch{
                            endCall(call)
                            goBackToMainScreen()
                        }
                    }
                    DefaultOnCallActionHandler.onCallAction(call, action)
                },
                onBackPressed = {
                    lifecycleScope.launch{
                        endCall(call)
                        goBackToMainScreen()
                    }
                }
            )

        }


        @Composable
        override fun StreamCallActivity.AudioCallContent(call: Call) {
            println("🎤 [CallActivity] Rendering AudioCallContent for call id = ${call.id}")
            val micEnabled by call.microphone.isEnabled.collectAsStateWithLifecycle()
            println("🎙 [CallActivity] Microphone enabled: $micEnabled")
            LaunchedEffect(call.id) {
                call.state.endedAt.collect { endedAt ->
                    if (endedAt != null) {
                        println("🛑 Call ended (remote or local)")
                        goBackToMainScreen()
                    }
                }
            }

            AudioOnlyCallContent(
                call = call,
                isMicrophoneEnabled = micEnabled,
                onCallAction = {
                    println("🎬 [CallActivity] User action in audio mode: $it")
                    if(it == LeaveCall) {
                        lifecycleScope.launch{
                            endCall(call)
                            goBackToMainScreen()
                        }
                    }
                    DefaultOnCallActionHandler.onCallAction(call, it)
                },
                onBackPressed = {
                    println("🔙 [CallActivity] onBackPressed in audio mode")
                    lifecycleScope.launch{
                        endCall(call)
                        goBackToMainScreen()
                    }
                },
            )
        }


        private fun StreamCallActivity.goBackToMainScreen() {
            println("🧭 [AcceptCallActivity] Navigating back to MainActivity... isFinishing = $isFinishing")

            if (!isFinishing) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)

                println("✔️ [AcceptCallActivity] MainActivity started, finishing CallActivity")
                safeFinish()
            }
        }
    }
}
