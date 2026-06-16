package com.example.chatter.features.contacts.domain.model

import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber


data class Contact(
    val userId : String,
    val name : String,
    val phoneNumber : PhoneNumber,
    val image : String?  = null,
    val colorScheme : ColorScheme? = null,
    val isUser : Boolean ,
)
