package com.example.chatter.features.chats.presentation.chats

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.chatter.R
import com.example.chatter.core.presentation.SearchEntriesTxtField
import com.example.chatter.core.presentation.SessionExpiredDialog
import com.example.chatter.features.chats.presentation.chats.component.AChat
import com.example.chatter.features.chats.presentation.chats.component.ChatsTopBar
import com.example.chatter.features.chats.presentation.chats.component.PermissionDialog
import com.example.chatter.features.chats.presentation.conversation.ConversationEvent
import com.example.chatter.ui.theme.blue400


@Preview(device = Devices.PIXEL_C, showBackground = true)
@PreviewLightDark
@Composable
fun ChatScreenPreview() {
    val rooms =  remember {
        mutableStateListOf<ChatRoom>().apply {
           // addAll(chatRooms)
        }
    }

}


private fun buildPermissionsToRequest(): Array<String> = buildList {
    add(Manifest.permission.RECORD_AUDIO)
    add(Manifest.permission.CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        add(Manifest.permission.BLUETOOTH_CONNECT)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(Manifest.permission.POST_NOTIFICATIONS)
    }
}.toTypedArray()


@Composable
fun ChatScreen(
    dialogQueue : SnapshotStateList<String> ,
    dialogState : Boolean ,
    chatRooms : SnapshotStateList<ChatRoom>,
    makeChatRoom : () -> Unit ,
    goSetting : () -> Unit  ,
    logOut : () -> Unit ,
    onAChatClick : (String) -> Unit ,
    onPermissionResult : ( permission : String , isGranted : Boolean) -> Unit ,
    onDismiss : () -> Unit ,
    navController : NavHostController ,
    refreshChats : () -> Unit
) {
    val permissionsToRequest = remember { buildPermissionsToRequest() }
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                onPermissionResult(
                   permission,
                    perms[permission] == true
                )
            }
        }
    )

    val context = LocalContext.current

    val activity = context as? Activity
    Scaffold (
        topBar = { ChatsTopBar(
            goSetting = { goSetting() }
        ) },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(60.dp),
                containerColor = blue400,
                contentColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp),
                onClick = makeChatRoom
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_24) ,
                    contentDescription = null ,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    ){innerPadding ->
        Surface (
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.surface
        ){
            val navBackStackEntry = remember {
                navController.currentBackStackEntry
            }
            val lifecycle = navBackStackEntry?.lifecycle
            DisposableEffect(lifecycle) {
                val observer = LifecycleEventObserver { _, event ->
                    when(event) {
                        Lifecycle.Event.ON_RESUME -> {
                            println("\uD83D\uDC3C [ConversationScreen] onResume()")
                            refreshChats()
                            // Re-check permission state after returning from
                            // System Settings; prune any granted ones from the queue.
                            permissionsToRequest.forEach { permission ->
                                val granted = ContextCompat.checkSelfPermission(
                                    context,
                                    permission
                                ) == PackageManager.PERMISSION_GRANTED
                                if (granted) onPermissionResult(permission, true)
                            }
                        }
                        else -> Unit
                    }
                }
                lifecycle?.addObserver(observer)

                onDispose {
                    println("\uD83D\uDC3C [ConversationScreen] onDispose()")
                    lifecycle?.removeObserver(observer)
                }
            }
            LaunchedEffect(Unit) {
                    multiplePermissionResultLauncher.launch(permissionsToRequest)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                stickyHeader {
                    SearchEntriesTxtField(
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp, bottom = 10.dp)
                            .fillMaxWidth(),
                        hint = "Search chat",
                        text = "",
                        onTextChange = {},
                        onClear = {},
                        onBack = {}
                    )
                }
                itemsIndexed(items = chatRooms.toList()) { index: Int, chatRoom: ChatRoom ->
                    AChat(
                        chatRoom = chatRoom,
                        onClick = {
                            chatRoom.roomId?.let(onAChatClick)
                        }
                    )
                }
                item {
                    if (chatRooms.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Chats Found",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = blue400
                            )
                        }
                    }
                }
            }
            if(dialogState){
                SessionExpiredDialog(
                    onDismiss = {},
                    onLoginAgain = logOut
                )
            }
            dialogQueue.firstOrNull()?.let { permission ->
                val provider = when (permission) {
                    Manifest.permission.CAMERA -> CameraPermissionTextProvider()
                    Manifest.permission.RECORD_AUDIO -> RecordAudioPermissionTextProvider()
                    Manifest.permission.BLUETOOTH_CONNECT -> BlueToothConnectPermissionTextProvider()
                    Manifest.permission.POST_NOTIFICATIONS -> PostNotificationPermissionTextProvider()
                    else -> return@let
                }
                PermissionDialog(
                    permissionTextProvider = provider,
                    isPermanentlyDeclined = activity?.let {
                        !shouldShowRequestPermissionRationale(it, permission)
                    } ?: false,
                    onDismiss = onDismiss,
                    onOkClick = {
                        onDismiss()
                        multiplePermissionResultLauncher.launch(arrayOf(permission))
                    },
                    onGoToAppSettingsClick = {
                        activity?.openAppSettings()
                    }
                )
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}