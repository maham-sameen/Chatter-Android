package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onEach

/**
 * Pipe-only: emits raw presence/typing frames from the socket.
 * State derivation lives in [com.example.chatter.features.chats.presentation.conversation.ConversationViewModel]
 * so every frame is applied to the CURRENT chat state via `MutableStateFlow.update { ... }`,
 * not a snapshot captured at subscription time.
 */
class ObserveMemberInfos(
    private val chatRepo: ChatRepo
) {
    operator fun invoke(): SharedFlow<Any?> {
        println("🛰️ [UC/ObserveMemberInfos] invoke() — exposing presence SharedFlow to viewmodel")
        return chatRepo.observerMemberInfo()
    }
}
