package com.example.chatter.features.chats.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.domain.model.User
import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity

class ContactEntityToUser : Mapper<ContactEntity , User> {
    override fun map(from: ContactEntity): User {
        return User(
            name = from.name,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            colorScheme = from.colorScheme?.let{
                ColorScheme(
                    background = from.colorScheme.background,
                    icon = from.colorScheme.icon
                )
            },
            image = from.image,
            lastSeen = from.lastSeen ?: 0
        )
    }
}