package com.example.chatter.features.chats.domain.model

import com.example.chatter.core.domain.model.PhoneNumber


data class Typing(
    val roomId : String,
    val phoneNumber: PhoneNumber,
    val isTyping: Boolean,
)
