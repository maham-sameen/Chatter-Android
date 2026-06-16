package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.chats.domain.model.Typing

class TypingToTypingDb : Mapper<Typing , com.example.chatter.features.chats.data.data_source.model.remote.Typing> {
    override fun map(from: Typing): com.example.chatter.features.chats.data.data_source.model.remote.Typing {
        return com.example.chatter.features.chats.data.data_source.model.remote.Typing(
            roomId = from.roomId,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            isTyping = from.isTyping
        )
    }
}