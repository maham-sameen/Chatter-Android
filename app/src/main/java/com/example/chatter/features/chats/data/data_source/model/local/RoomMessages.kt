package com.example.chatter.features.chats.data.data_source.model.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.chatter.core.data.data_source.model.local.ChatRoomEntity

data class RoomMessages(
    @Embedded
    val chatRoom : ChatRoomEntity,

    @Relation(
        parentColumn = "roomId" ,
        entityColumn = "roomId"
    )
    val messages : List<MessageEntity>
)