package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.chats.data.data_source.model.local.MessageEntity
import com.example.chatter.features.chats.domain.model.Message

class MessageDomainToMessageEntity(
    private val roomId : String
) : Mapper<Message , MessageEntity> {
    override fun map(from: Message): MessageEntity {
        return MessageEntity(
            id = from.id,
            roomId = roomId,
            text = from.text,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            timeStamp = from.timeStamp ,
            isRead = from.isRead
        )
    }
}