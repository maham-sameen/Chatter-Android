package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.features.chats.domain.repository.OpenedChatMsg

class ChatOpen(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(
        roomId : String ,
    ){
        println("[ChatOpen] roomId : $roomId , userPhoneNumber ")
        chatRepo.sendOpenedChatMsg(
            openChatMsg = OpenedChatMsg(
                roomId = roomId,
            )
        )
    }
}