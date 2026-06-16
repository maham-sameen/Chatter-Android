package com.example.chatter.features.chats.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import kotlinx.serialization.Serializable

@Serializable
data class ConversationRoom(
    val roomId : String,
    val image  : String? = null,
    val name : String,
    val timeStamp : Long,
    val colorScheme: ColorScheme,
    val members  : List<UserInfo> ,
    val lastMessage : String , val unreadMessageCount : Int
)
