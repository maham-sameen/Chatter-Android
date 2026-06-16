package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.chats.data.data_source.model.local.MessageEntity
import com.example.chatter.features.chats.data.data_source.model.remote.Message

class MessageToMessageEntity : Mapper<Message , MessageEntity> {
    override fun map(from: Message): MessageEntity {
        return MessageEntity(
            roomId = from.roomId,
            text = from.text,
            phoneNumber = from.phoneNumber,
            timeStamp = from.timeStamp,
            id = from.id ,
            isRead = from.isRead
        )
    }
}