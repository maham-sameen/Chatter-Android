package com.example.chatter.features.chats.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.model.remote.BsonDateTimeSerializer
import kotlinx.serialization.Serializable
import org.bson.BsonDateTime

@Serializable
sealed class IncomingFrame {

    @Serializable
    data class OpenChatMsg(
        val room_id : String,
        val userPhoneNumber: PhoneNumber,
        @Serializable(with = BsonDateTimeSerializer::class)
        val timeStamp: BsonDateTime,
    ) : IncomingFrame()

    @Serializable
    data class Text(
        val raw : String
    ) : IncomingFrame()

    @Serializable
    data class MemberInfo(
        val phoneNumber : PhoneNumber,
        val online : Boolean,
        val lastSeen : Long,
        val isTyping : Boolean
    )
}