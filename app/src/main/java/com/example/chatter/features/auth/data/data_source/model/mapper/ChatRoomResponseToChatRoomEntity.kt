package com.example.chatter.features.auth.data.data_source.model.mapper

import com.example.chatter.core.data.data_source.model.local.ChatRoomEntity
import com.example.chatter.core.data.data_source.model.remote.ChatRoomResponse
import com.example.chatter.core.data.data_source.utils.Mapper

class ChatRoomResponseToChatRoomEntity : Mapper<ChatRoomResponse , ChatRoomEntity> {
    override fun map(from: ChatRoomResponse): ChatRoomEntity {
        return ChatRoomEntity(
            roomId = from.roomId,
            timeStamp = from.timeStamp,
            image = from.image,
            name = from.name,
            colorScheme  = from.colorScheme ,
            lastMessage = from.lastMessage ,
            unreadMessages = from.unreadMessageCount
        )
    }
}