package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.domain.model.Presence
import com.example.chatter.features.chats.domain.model.Typing
import com.example.chatter.features.chats.domain.repository.ChatRepo
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class SendMemberInfo(
    private val chatRepo: ChatRepo
) {

    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        roomId : String ,
        sendPresence : Boolean  ,
        isTyping : Boolean
    ){
        println("\uD83C\uDF4A SendMemberInfo , invoke  , roomId : $roomId , sendPresence : $sendPresence , isTyping : $isTyping")
        val phoneNumber = PhoneNumber.fromString(chatRepo.getUserPhoneNumber())
        if(sendPresence){
            println("\uD83C\uDF4A SendMemberInfo , invoke  , sendPresence() ")
            chatRepo.sendMemberInfo(
                memberInfo = Presence(
                    roomId = roomId,
                    phoneNumber = phoneNumber,
                    online = true,
                    lastSeen = Clock.System.now().toEpochMilliseconds()
                )
            )
        }else{
            println("\uD83C\uDF4A SendMemberInfo , invoke  , sendTyping() ")
            chatRepo.sendMemberInfo(
                memberInfo = Typing(
                    roomId = roomId,
                    phoneNumber = phoneNumber,
                    isTyping = isTyping
                )
            )
        }
    }
}