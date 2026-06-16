package com.example.chatter.features.chats.presentation.conversation.model

import com.example.chatter.ui.ColorScheme


/**
 * we show like
 * maham , fariha , etc if there is group
 * otherwise last seen or online or typing
 */
data class ChatState(
    val name : String ,
    val image : String? = null ,
    val colorScheme: ColorScheme ,
    val lastSeen : String ,
    val isOnline : Boolean ,
    val isTyping : Boolean ,
    val members : List<Member>  = emptyList(),
    val membersNames : String ,
    val roomId : String ,
    val presenceText : String ,
)