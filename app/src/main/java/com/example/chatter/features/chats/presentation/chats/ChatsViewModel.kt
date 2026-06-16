package com.example.chatter.features.chats.presentation.chats

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatter.features.chats.domain.use_case.chats.ChatsUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val chatsUseCases: ChatsUseCases
) : ViewModel() {

    private var _dialogState = mutableStateOf(false)
    val dialogState: State<Boolean> = _dialogState

    val chatRooms: StateFlow<SnapshotStateList<ChatRoom>> = chatsUseCases.getAllChats()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            mutableStateListOf()
        )

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        if (visiblePermissionDialogQueue.isNotEmpty()) {
            visiblePermissionDialogQueue.removeAt(0)
        }
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (isGranted) {
            visiblePermissionDialogQueue.remove(permission)
        } else if (!visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun refreshChats() {
        viewModelScope.launch {
            chatRooms.value.forEach { room ->
                room.roomId?.let { chatsUseCases.updateChatRoom(it) }
            }
        }
    }
}
