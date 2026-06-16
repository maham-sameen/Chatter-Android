package com.example.chatter.core.data.data_source.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.chatter.core.data.data_source.model.local.ColorScheme
import kotlinx.serialization.SerialName

@Entity(
    tableName = "chats" ,
    indices = [
        Index(value = ["roomId"], unique = true),
    ]
)
data class ChatRoomEntity (
    @PrimaryKey(autoGenerate = true)
    @SerialName("_id")
    val _id : Long = 0,
    val roomId : String,
    val timeStamp : Long,
    val image : String? = null,
    val name : String ,
    @Embedded("_color")
    val colorScheme : ColorScheme? = null ,
    val lastMessage : String = "" ,
    val unreadMessages : Int = 0 ,
)