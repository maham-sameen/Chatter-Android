package com.example.chatter.features.chats.domain.model

import com.example.chatter.core.domain.model.PhoneNumber


data class ChatOpened(
    val phoneNumber: PhoneNumber,
    val roomId : String ,
    val chatOpened : Boolean
)
