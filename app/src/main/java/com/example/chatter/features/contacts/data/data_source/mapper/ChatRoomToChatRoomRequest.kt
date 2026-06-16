package com.example.chatter.features.contacts.data.data_source.mapper

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.contacts.data.data_source.model.remote.ChatRoomRequest
import com.example.chatter.features.contacts.domain.model.ChatRoom

class ChatRoomToChatRoomRequest : Mapper<ChatRoom , ChatRoomRequest> {
    override fun map(from: ChatRoom): ChatRoomRequest {
        return ChatRoomRequest(
            members = from.members.map {
                PhoneNumber(
                    countryCode = it.countryCode,
                    phoneNumber = it.phoneNumber
                )
            },
            name = from.name,
            image = from.image,
            colorScheme = ColorScheme(
                background = from.colorScheme.background,
                icon = from.colorScheme.icon
            )
        )
    }
}