package com.example.chatter.core.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomResponse(
    val name : String,
    val image : String? = null,
    val colorScheme: ColorScheme,
    val timeStamp : Long,
    val roomId : String ,
    val lastMessage : String ,
    val unreadMessageCount : Int
)