package com.example.chatter.features.chats.data.data_source.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.chatter.core.data.data_source.model.local.ChatRoomEntity
import com.example.chatter.core.data.data_source.model.local.PhoneNumber

@Entity(
    tableName = "messages" ,
    foreignKeys = [
        ForeignKey(
            entity = ChatRoomEntity::class,
            parentColumns = ["roomId"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = false)
    val id : String,
    val roomId : String,
    val text : String,
    @Embedded(prefix = "_phoneNo_")
    val phoneNumber: PhoneNumber,
    val timeStamp : Long,
    val isRead : Boolean = false ,
)