package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.model.local.User
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber

class UserDbToUser : Mapper<User, com.example.chatter.features.chats.domain.model.User> {
    override fun map(
        from: User,
    ): com.example.chatter.features.chats.domain.model.User {
        return com.example.chatter.features.chats.domain.model.User(
            image = from.image,
            colorScheme = ColorScheme(
                background = from.colorScheme.background,
                icon = from.colorScheme.icon
            ),
            name = from.name,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            lastSeen = 0
        )
    }
}