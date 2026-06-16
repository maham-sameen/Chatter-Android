package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.remote.ChatOpened

class ChatOpenedDbToChatOpened : Mapper<ChatOpened , com.example.chatter.features.chats.domain.model.ChatOpened> {
    override fun map(from: ChatOpened): com.example.chatter.features.chats.domain.model.ChatOpened {
        return com.example.chatter.features.chats.domain.model.ChatOpened(
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            roomId = from.roomId,
            chatOpened = from.chatOpened
        )
    }
}