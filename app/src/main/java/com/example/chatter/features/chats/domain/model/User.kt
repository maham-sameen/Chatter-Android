package com.example.chatter.features.chats.domain.model

import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber

data class User(
    val image : String? = null,
    val colorScheme: ColorScheme?,
    val name : String,
    val phoneNumber : PhoneNumber ,
    val lastSeen : Long
)
