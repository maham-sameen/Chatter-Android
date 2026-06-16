package com.example.chatter.features.chats.domain.model

import com.example.chatter.core.domain.model.PhoneNumber

data class Message(
    val id : String ,
    val text : String,
    val phoneNumber : PhoneNumber,
    val timeStamp : Long ,
    val isRead : Boolean = false
)
