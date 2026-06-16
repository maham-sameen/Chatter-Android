package com.example.chatter.core.data.data_source.model.local

import androidx.room.Embedded
import androidx.room.Relation

data class ConversationRoom(
    @Embedded
    val chatRoomEntity: ChatRoomEntity ,

    @Relation(
        parentColumn = "roomId" ,
        entityColumn = "roomId"
    )
    val members : List<MemberEntity>
)
