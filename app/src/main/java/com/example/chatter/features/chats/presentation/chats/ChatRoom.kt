package com.example.chatter.features.chats.presentation.chats

import com.example.chatter.ui.ColorScheme

data class ChatRoom(
    val roomId : String? = null,
    val profilePath  : String? = null ,
    val name : String ,
    val timeStamp : String ,
    val lastMessage : String ,
    val color : ColorScheme ,
    val unreadMessages : Int
)