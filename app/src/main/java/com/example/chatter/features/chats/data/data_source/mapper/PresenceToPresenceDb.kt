package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.chats.domain.model.Presence

class PresenceToPresenceDb : Mapper<Presence , com.example.chatter.features.chats.data.data_source.model.remote.Presence> {
    override fun map(from: Presence): com.example.chatter.features.chats.data.data_source.model.remote.Presence {
        return com.example.chatter.features.chats.data.data_source.model.remote.Presence(
            roomId = from.roomId,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            online = from.online,
            lastSeen = from.lastSeen
        )
    }
}