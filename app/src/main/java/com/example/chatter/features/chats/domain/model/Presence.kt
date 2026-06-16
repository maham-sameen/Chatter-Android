package com.example.chatter.features.chats.domain.model

import com.example.chatter.core.domain.model.PhoneNumber


data class Presence(
    val roomId : String,
    val phoneNumber: PhoneNumber,
    val online : Boolean,
    val lastSeen : Long,
)
