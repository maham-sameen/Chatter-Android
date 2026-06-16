package com.example.chatter.features.chats.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable


@Serializable
data class GetUpdatedChatRoomRequest(
    val roomId : String ,
    val phoneNumber : PhoneNumber
)
