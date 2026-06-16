package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.remote.Typing

class TypingDbToTyping : Mapper<Typing , com.example.chatter.features.chats.domain.model.Typing> {
    override fun map(from: Typing): com.example.chatter.features.chats.domain.model.Typing {
        return com.example.chatter.features.chats.domain.model.Typing(
            roomId = from.roomId,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            isTyping = from.isTyping
        )
    }
}