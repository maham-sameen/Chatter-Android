package com.example.chatter.features

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.chatter.core.presentation.utils.Graph
import com.example.chatter.features.calls.data.data_source.remote.CallService
import com.example.chatter.features.calls.presentation.call_history.CallHistoryScreen
import com.example.chatter.features.calls.presentation.call_history.CallHistoryViewModel
import com.example.chatter.features.chats.presentation.chats.ChatScreen
import com.example.chatter.features.chats.presentation.chats.ChatsViewModel
import com.example.chatter.features.chats.presentation.conversation.Conversation
import com.example.chatter.features.chats.presentation.conversation.ConversationEvent
import com.example.chatter.features.chats.presentation.conversation.ConversationViewModel
import com.example.chatter.features.chats.presentation.settingNavGraph
import com.example.chatter.features.chats.presentation.utils.Settings
import com.example.chatter.features.contacts.presentation.contacts.ContactsEvent
import com.example.chatter.features.contacts.presentation.contacts.ContactsScreen
import com.example.chatter.features.contacts.presentation.contacts.ContactsViewModel
import com.example.chatter.features.contacts.presentation.contactsNavigation
import com.example.chatter.features.contacts.presentation.create_chat.CreateChatScreen
import com.example.chatter.features.contacts.presentation.create_chat.CreateChatViewModel
import com.example.chatter.features.contacts.presentation.util.ContactScreen
import com.example.chatter.features.contacts.presentation.util.CreateChat
import com.example.chatter.features.utils.BottomBarScreen
import kotlinx.serialization.json.Json
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel




@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun HomeNavGraph(
    navController : NavHostController ,
    logOut : () -> Unit
) {
    NavHost(
        navController = navController ,
        startDestination = BottomBarScreen.Chats.route
    ){
        composable(route = BottomBarScreen.Chats.route) {
            val viewModel = koinViewModel<ChatsViewModel>()
            val chats = viewModel.chatRooms.collectAsStateWithLifecycle()
            ChatScreen(
                goSetting = {
                    navController.navigate(Settings.Profile)
                },
                chatRooms = chats.value,
                makeChatRoom = {
                    navController.currentBackStackEntry?.savedStateHandle
                        ?.set("make_chat_room", true)

                    navController.navigate(BottomBarScreen.Contacts.route)
                },
                dialogState = viewModel.dialogState.value,
                logOut = logOut,
                onAChatClick = {
                    navController.navigate(Graph.Conversation(it))
                },
                dialogQueue = viewModel.visiblePermissionDialogQueue,
                onPermissionResult = viewModel::onPermissionResult,
                onDismiss = viewModel::dismissDialog,
                navController = navController,
                refreshChats = viewModel::refreshChats,
            )
        }
        composable(route = BottomBarScreen.Contacts.route) {
            // Read once and remember it for this session
            val initialMakeChatRoom = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Boolean>("make_chat_room") ?: false

            // Remember it in this screen so it stays stable
            val makeChatRoom by remember { mutableStateOf(initialMakeChatRoom) }

            // Now safely remove it — after you've remembered it
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("make_chat_room")
            println("\uD83E\uDDF6 contacts makeContactRoom : $makeChatRoom")
            val viewModel = koinViewModel<ContactsViewModel>()
            val editContacts = viewModel.editContacts.collectAsStateWithLifecycle()
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            ContactsScreen(
                contacts = editContacts.value,
                makeChatRoom = makeChatRoom,
                onToggleSelection = {
                    if (makeChatRoom) {
                        viewModel.onEvent(ContactsEvent.OnToggleSelection(it))
                    }
                },
                createContact = {
                    navController.navigate(ContactScreen.CreateContact)
                },
                onMakeChatRoom = { contactsPhoneNumbers ->
                    // move to create group
                    val json = Json.encodeToString(contactsPhoneNumbers.toList())
                    navController.navigate(CreateChat(json))
                },
                makeAChat = {
                    viewModel.onEvent(ContactsEvent.CreateAChat)
                },
                uiState = uiState.value,
                clearUIState = { viewModel.onEvent(ContactsEvent.ClearUIState) },
                onSuccess = {
                    navController.navigate(BottomBarScreen.Chats.route)
                },
            )
        }
        composable(route = BottomBarScreen.Status.route) {
            Box(modifier = Modifier.fillMaxSize()){
                Text("Status")
            }
        }
        composable(route = BottomBarScreen.Calls.route) {
            val viewModel = koinViewModel<CallHistoryViewModel>()
            CallHistoryScreen(
                viewModel
            )
        }

        contactsNavigation(
            navController = navController,
            startDestination = ContactScreen.CreateContact,
            onSaveContact = {
                navController.navigate(BottomBarScreen.Contacts.route)
            }
        )

        composable<CreateChat>(
        ) {backStackEntry ->
            val viewModel = koinViewModel<CreateChatViewModel>()
            CreateChatScreen(
                viewModel = viewModel,
                onSuccess = {
                    navController.popBackStack(BottomBarScreen.Chats.route, inclusive = false)
                }
            )
        }

        composable<Graph.Conversation> {backStackEntry ->
            val viewModel = koinViewModel<ConversationViewModel>()
            val callService  =  koinInject<CallService>()
            // ✅ You can access the typed route object directly
            val args = backStackEntry.toRoute<Graph.Conversation>()
            LaunchedEffect(Unit) {
                println("\uD83C\uDFC9 [HomeNavGraph] LaunchedEffect  , args : ${args.toString()} , chatState : ${viewModel.chatState.value}")
                viewModel.onEvent(ConversationEvent.GetChatState(
                    roomId = args.roomId,
                ))
            }
            val context = LocalContext.current
            Conversation(
                viewModel = viewModel,
                onVideoCall = {
                    callService.startOutgoingCallActivity(
                        callees = it,
                        isVideoCall = true,
                        context = context
                    )
                },
                onAudioCall = {
                    callService.startOutgoingCallActivity(
                        callees = it,
                        isVideoCall = false,
                        context = context
                    )
                },
                navController = navController,
            )
        }

        settingNavGraph(
            navController = navController,
            logOut = logOut,
            onBackPressed = {
                navController.navigate(BottomBarScreen.Chats.route)
            }
        )
    }
}
