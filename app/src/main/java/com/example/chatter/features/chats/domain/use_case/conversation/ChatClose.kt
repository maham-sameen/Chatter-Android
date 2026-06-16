package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo

class ChatClose(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(){
        println("🚪 [UC/ChatClose] closing chat socket")
        chatRepo.closeSession()
        println("🚪 [UC/ChatClose] closed")
    }
}