package com.example.chatter.features.chats.domain.use_case.chats



data class ChatsUseCases (
    val isSessionExpired : IsSessionExpired,
    val getAllChats: GetAllChats ,
    val updateChatRoom: UpdateChatRoom ,
)