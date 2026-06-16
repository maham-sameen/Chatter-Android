package com.example.chatter.features.chats.domain.use_case.chats

import com.example.chatter.features.chats.domain.repository.ChatRepo

class UpdateChatRoom(
    private val chatRepo: ChatRepo
) {

    suspend operator fun invoke(roomId : String){
        chatRepo.updateChatRoom(roomId = roomId)
    }
}