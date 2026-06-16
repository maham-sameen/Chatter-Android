package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.local.MessageEntity
import com.example.chatter.features.chats.domain.model.Message

class MessageEntityToMessage : Mapper<MessageEntity , Message> {
    override fun map(from: MessageEntity): Message {
        return Message(
            text = from.text,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            timeStamp = from.timeStamp,
            id = from.id ,
            isRead = from.isRead
        )
    }
}