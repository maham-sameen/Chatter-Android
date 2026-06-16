package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo
import kotlinx.coroutines.flow.SharedFlow

/**
 * Pipe-only: emits raw message/chat-opened frames from the chat socket.
 * State derivation lives in the ViewModel so every frame is applied
 * to the current chat state via `MutableStateFlow.update { ... }`.
 */
class ObserveMessage(
    private val chatRepo: ChatRepo
) {
    operator fun invoke(): SharedFlow<Any?> {
        println("🛰️ [UC/ObserveMessage] invoke() — exposing message SharedFlow to viewmodel")
        return chatRepo.observeMessage()
    }
}
