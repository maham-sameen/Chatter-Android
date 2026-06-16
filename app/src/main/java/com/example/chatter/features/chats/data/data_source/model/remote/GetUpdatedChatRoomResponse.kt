package com.example.chatter.features.chats.data.data_source.model.remote

import kotlinx.serialization.Serializable


@Serializable
data class GetUpdatedChatRoomResponse(
    val timeStamp : Long,
    val lastMessage : String = "" ,
    val unreadMessages : Int = 0 ,
)
