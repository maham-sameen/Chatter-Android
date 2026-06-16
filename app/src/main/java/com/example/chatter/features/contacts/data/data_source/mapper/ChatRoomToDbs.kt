package com.example.chatter.features.contacts.data.data_source.mapper

import com.example.chatter.core.data.data_source.model.local.ChatRoomEntity
import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.MemberEntity
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.contacts.domain.model.ChatRoom

class ChatRoomToDbs : Mapper<ChatRoom , Pair<ChatRoomEntity , List<MemberEntity>>> {
    override fun map(from: ChatRoom): Pair<ChatRoomEntity, List<MemberEntity>> {
        return Pair(
            first = ChatRoomEntity(
                roomId = from.roomId,
                timeStamp = from.timeStamp!!,
                image = from.image,
                name = from.name,
                colorScheme = ColorScheme(
                    background = from.colorScheme.background,
                    icon = from.colorScheme.icon
                )
            ),
            second = from.members.map {
                println("ChatRoomToDbs , members : ${it.toString()}")
                MemberEntity(
                    roomId = from.roomId,
                    phoneNumber = PhoneNumber(
                        countryCode = it.countryCode,
                        phoneNumber = it.phoneNumber
                    )
                )
            }
        )
    }
}