package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.model.Message
import com.example.chatter.features.chats.domain.repository.ChatRepo

class SaveIncomingMessage(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(message: Message, roomId: String) {
        println("💌 [UC/SaveIncomingMessage] roomId=$roomId message=$message")
        chatRepo.saveMessage(message = message, roomId = roomId)
        println("💌 [UC/SaveIncomingMessage] saved")
    }
}
