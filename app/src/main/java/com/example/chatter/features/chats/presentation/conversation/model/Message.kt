package com.example.chatter.features.chats.presentation.conversation.model

data class Message(
    val id : String,
    val text : String ,
    val formattedTime : String ,
    val username : String ,
    val status: Boolean = false ,
    val dateGroup : String
)
