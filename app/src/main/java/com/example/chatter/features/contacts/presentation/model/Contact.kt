package com.example.chatter.features.contacts.presentation.model

import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.ui.ColorScheme


data class Contact(
    val name : String,
    val color : ColorScheme,
    val phoneNumber : PhoneNumber,
    val image : String? = null,
    val isSelected : Boolean = false
)