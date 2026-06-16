package com.example.chatter.features.contacts.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.data.data_source.model.remote.ChatRoomResponse
import com.example.chatter.features.contacts.domain.model.ChatRoom

class ChatRoomResponseToChatRoom : Mapper<ChatRoomResponse, ChatRoom> {
    override fun map(from: ChatRoomResponse): ChatRoom {
        return ChatRoom(
            roomId = "",
            name = from.name,
            image = from.image,
            colorScheme = ColorScheme(
                background = from.colorScheme.background,
                icon = from.colorScheme.icon
            ),
            members = listOf(),
            timeStamp = from.timeStamp
        )
    }
}