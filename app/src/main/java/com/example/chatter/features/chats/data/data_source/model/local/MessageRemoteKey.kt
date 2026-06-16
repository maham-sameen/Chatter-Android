package com.example.chatter.features.chats.data.data_source.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages_remote_keys"
)
data class MessageRemoteKey (
    @PrimaryKey(autoGenerate = false)
    val id : String,
    val prevKey : Int? ,
    val nextKey : Int? ,
    val roomId : String
)