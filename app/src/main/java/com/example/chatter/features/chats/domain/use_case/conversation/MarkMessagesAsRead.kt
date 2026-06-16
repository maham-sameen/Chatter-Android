package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo

class MarkMessagesAsRead(
    private val chatRepo: ChatRepo
) {

    suspend operator fun invoke(roomId : String){
        println("📖 [UC/MarkMessagesAsRead] roomId=$roomId")
        chatRepo.markMessagesAsRead(roomId)
        println("📖 [UC/MarkMessagesAsRead] done")
    }
}