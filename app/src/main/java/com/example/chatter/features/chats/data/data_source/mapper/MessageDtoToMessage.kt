package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.remote.Message

class MessageDtoToMessage() : Mapper<Message , com.example.chatter.features.chats.domain.model.Message> {
    override fun map(from: Message): com.example.chatter.features.chats.domain.model.Message {
        return com.example.chatter.features.chats.domain.model.Message(
            text = from.text,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            timeStamp = from.timeStamp,
            id = from.id,
            isRead = from.isRead
        )
    }
}