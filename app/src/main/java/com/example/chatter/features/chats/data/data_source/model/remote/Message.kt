package com.example.chatter.features.chats.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val roomId : String,
    val text : String,
    val phoneNumber : PhoneNumber,
    val timeStamp : Long,
    val id : String ,
    val isRead : Boolean = false
)
