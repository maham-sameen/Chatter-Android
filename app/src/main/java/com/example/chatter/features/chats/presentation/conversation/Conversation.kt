package com.example.chatter.features.chats.presentation.conversation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatter.R
import com.example.chatter.core.presentation.LoadingAnimation
import com.example.chatter.core.presentation.ShowUIState
import com.example.chatter.core.presentation.TxtField
import com.example.chatter.core.presentation.showSnackBarUI
import com.example.chatter.features.chats.presentation.conversation.component.ConversationTopBar
import com.example.chatter.features.chats.presentation.conversation.component.Messages
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Conversation(
    navController : NavHostController ,
    viewModel: ConversationViewModel ,
    onVideoCall : (callees : List<String>) -> Unit ,
    onAudioCall : (callees : List<String>) -> Unit
) {
    val uiState by  viewModel.uiState.collectAsStateWithLifecycle()
    val chatState by viewModel.chatState.collectAsStateWithLifecycle()
    val message by  viewModel.message
    val paging =  viewModel.messages.collectAsLazyPagingItems()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val callees = viewModel.callees
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState() // <-- track scroll


    LaunchedEffect(paging.itemCount) {
        if (paging.itemCount > 0) {
            listState.animateScrollToItem(0)
        }
    }
    var floatingDate by remember { mutableStateOf<String?>(null) }
    var hideJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->

                if (visibleItems.isEmpty()) return@collect

                val topIndex = visibleItems.first().index
                val message = paging.peek(topIndex) ?: return@collect

                // If same date, don't restart animation
                if (floatingDate != message.dateGroup) {
                    floatingDate = message.dateGroup

                    // Cancel previous hide timer and restart
                    hideJob?.cancel()
                    hideJob = coroutineScope.launch {
                        delay(1200) // WhatsApp feel
                        floatingDate = null
                    }
                }
            }
    }
    Scaffold (
        modifier = Modifier.imePadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        } ,
        topBar = {
            ConversationTopBar(
                name = chatState.name,
                image = chatState.image,
                presenceText = chatState.presenceText,
                onVoiceCall = {
                    println("\uD83D\uDE8D Conversation Screen , onVoiceCall , callees : $callees")
                    onAudioCall(callees.toList())
                              },
                onVideoCall = {
                    println("\uD83D\uDE8D Conversation Screen , onVideoCall , callees : $callees")
                    onVideoCall(callees.toList())
                }
            )
        }
    ){ innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ,
            color = MaterialTheme.colorScheme.surfaceContainerLowest
        ) {
            val navBackStackEntry = remember {
                navController.currentBackStackEntry
            }
            val lifecycle = navBackStackEntry?.lifecycle
            DisposableEffect(lifecycle) {
                val observer = LifecycleEventObserver { _, event ->
                    when(event) {
                        Lifecycle.Event.ON_RESUME -> {
                            println("\uD83D\uDC3C [ConversationScreen] onResume()")
                            paging.refresh()
                            viewModel.onEvent(ConversationEvent.ChatOpen)
                        }
                        Lifecycle.Event.ON_STOP -> {
                            println("\uD83D\uDC3C [ConversationScreen] onStop()")
                            viewModel.onEvent(ConversationEvent.ChatClose)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Messages(
                    modifier = Modifier.weight(1f),
                    paging = paging,
                    listState = listState,
                    floatingData = floatingDate
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TxtField(
                        hint = "Enter Message",
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.send_alt_1_svgrepo_com) ,
                                contentDescription = null ,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        viewModel.onEvent(ConversationEvent.SendMessage)
                                        if (paging.itemCount > 0) {
                                            scope.launch {
                                                delay(100)
                                                listState.animateScrollToItem(0)
                                            }
                                        }
                                    }
                            )
                        },
                        text = message,
                        onTextChanged = {
                            viewModel.onEvent(ConversationEvent.EnterMessage(it))
                        },
                        isError = false,
                        isFocused = false,
                        onFocusChanged = {},
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done ,
                        ),
                        onDone = true,
                        onDoneAction = {
                            viewModel.onEvent(ConversationEvent.SendMessage)
                            if (paging.itemCount > 0) {
                                scope.launch{ listState.animateScrollToItem(0) }
                            }
                        },
                    )
                }
            }
        }
        ShowUIState(
            uiState = uiState,
            onSuccess = {},
            showLoading = { LoadingAnimation() },
            showMessage = {message ->
                LaunchedEffect(message) {
                    showSnackBarUI(
                        snackBarHostState = snackBarHostState,
                        message = message,
                        actionLabel = "",
                        onDismiss = {},
                        onActionPerformed = {}
                    )
                    delay(1000)
                    viewModel.onEvent(ConversationEvent.ClearUIState)
                }
            }
        )
    }
}

