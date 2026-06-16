package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.remote.ConversationRoom
import com.example.chatter.features.chats.domain.model.User

class ConversationRoomDtoToConversationRoom : Mapper<ConversationRoom , com.example.chatter.features.chats.domain.model.ConversationRoom> {
    override fun map(from: ConversationRoom): com.example.chatter.features.chats.domain.model.ConversationRoom {
        return com.example.chatter.features.chats.domain.model.ConversationRoom(
            roomId = from.roomId,
            image = from.image,
            name = from.name,
            timeStamp = from.timeStamp,
            colorScheme = ColorScheme(
                background = from.colorScheme.background,
                icon = from.colorScheme.icon
            ),
            members = from.members.map {
                User(
                    image = it.image,
                    colorScheme = ColorScheme(
                        background = it.colorScheme.background,
                        icon = it.colorScheme.icon
                    ),
                    name = it.name,
                    phoneNumber = PhoneNumber(
                        countryCode = it.phoneNumber.countryCode,
                        phoneNumber = it.phoneNumber.phoneNumber
                    ),
                    lastSeen = it.lastSeen
                )
            },
            lastMessage = from.lastMessage,
            unreadMessageCount = from.unreadMessageCount,
        )
    }
}