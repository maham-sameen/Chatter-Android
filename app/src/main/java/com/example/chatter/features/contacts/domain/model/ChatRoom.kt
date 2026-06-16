package com.example.chatter.features.contacts.domain.model

import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber

data class ChatRoom(
    val roomId : String ,
    val name : String ,
    val image : String? = null ,
    val colorScheme: ColorScheme ,
    val members : List<PhoneNumber> ,
    val timeStamp : Long? = null
)
