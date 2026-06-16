package com.example.chatter.features.chats.presentation.conversation

sealed class ConversationEvent {

    data class GetChatState(
        val roomId : String ,
    ) : ConversationEvent()

    object ClearUIState : ConversationEvent()

    data class EnterMessage(val text : String) : ConversationEvent()

    object SendMessage : ConversationEvent()

    object ChatClose : ConversationEvent()

    object ChatOpen : ConversationEvent()
}