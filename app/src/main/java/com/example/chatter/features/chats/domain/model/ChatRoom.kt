package com.example.chatter.features.chats.domain.model

import com.example.chatter.core.domain.model.ColorScheme

data class ChatRoom(
    val roomId : String,
    val image  : String? = null ,
    val name : String ,
    val timeStamp : Long ,
    val colorScheme: ColorScheme? = null ,
    val lastMessage : String = "" ,
    val unreadMessages : Int = 0 ,
)
