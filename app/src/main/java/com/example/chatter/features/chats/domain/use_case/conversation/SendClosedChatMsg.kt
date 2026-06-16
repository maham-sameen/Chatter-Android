package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.model.ClosedChatMsg
import com.example.chatter.features.chats.domain.repository.ChatRepo
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SendClosedChatMsg(
    private val chatRepo: ChatRepo
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        roomId : String ,
    ){
        println("\uD83E\uDD4A SendOpenChatMessage , roomId : $roomId")
        chatRepo.sendClosedChatMsg(
            closedChatMsg = ClosedChatMsg(
                roomId = roomId,
                timeStamp = Clock.System.now().toEpochMilliseconds()
            )
        )
    }
}