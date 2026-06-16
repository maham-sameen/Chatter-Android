package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.remote.Presence

class PresenceDbToPresence : Mapper<Presence , com.example.chatter.features.chats.domain.model.Presence> {
    override fun map(from: Presence): com.example.chatter.features.chats.domain.model.Presence {
        return com.example.chatter.features.chats.domain.model.Presence(
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