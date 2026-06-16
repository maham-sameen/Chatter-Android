package com.example.chatter.features.chats.domain.model

import com.example.chatter.core.domain.model.ColorScheme

data class ConversationRoom(
    val roomId : String,
    val image  : String? = null ,
    val name : String ,
    val timeStamp : Long ,
    val colorScheme: ColorScheme? ,
    val members  : List<User> ,
    val lastMessage : String = "" , val unreadMessageCount : Int = 0
)
