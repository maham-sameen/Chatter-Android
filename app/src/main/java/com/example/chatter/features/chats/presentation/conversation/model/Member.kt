package com.example.chatter.features.chats.presentation.conversation.model

import com.example.chatter.core.presentation.model.PhoneNumber

data class Member(
    val phoneNumber: PhoneNumber ,
    val name : String ,
    val isOnline : Boolean = false
)
