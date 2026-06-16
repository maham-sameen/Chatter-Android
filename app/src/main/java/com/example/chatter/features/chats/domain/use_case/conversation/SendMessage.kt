package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo

class SendMessage(
    private val chatRepo: ChatRepo ,
) {
    suspend operator fun invoke(text : String){
        println("[SendMessage]  text : $text")
        chatRepo.sendMessage(text)
    }
}