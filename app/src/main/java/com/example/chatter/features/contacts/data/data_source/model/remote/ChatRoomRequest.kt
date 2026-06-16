package com.example.chatter.features.contacts.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomRequest(
    val members : List<PhoneNumber>,
    val name : String,
    val image : String? = null,
    val colorScheme: ColorScheme,
)
