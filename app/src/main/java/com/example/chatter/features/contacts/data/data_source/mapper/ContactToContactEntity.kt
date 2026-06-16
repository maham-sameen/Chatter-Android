package com.example.chatter.features.contacts.data.data_source.mapper

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity
import com.example.chatter.features.contacts.domain.model.Contact
import com.example.chatter.core.data.data_source.utils.Mapper

class ContactToContactEntity(
) : Mapper<Contact , ContactEntity> {
    override fun map(from: Contact): ContactEntity {
        return ContactEntity(
            userId = from.userId,
            name = from.name,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            isUser = from.isUser,
            image = from.image,
            colorScheme = from.colorScheme?.let {
                ColorScheme(
                    background = it.background,
                    icon = it.icon
                )
            }
        )
    }
}