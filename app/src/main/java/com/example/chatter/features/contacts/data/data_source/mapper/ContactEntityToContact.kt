package com.example.chatter.features.contacts.data.data_source.mapper

import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity
import com.example.chatter.features.contacts.domain.model.Contact
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber

class ContactEntityToContact(
) : Mapper<ContactEntity , Contact> {
    override fun map(from: ContactEntity): Contact {
        return Contact(
            userId = from.userId,
            name = from.name,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ) ,
            image = from.image,
            colorScheme = from.colorScheme?.let{
                ColorScheme(
                    background = it.background,
                    icon = it.icon
                )
            },
            isUser = from.isUser
        )
    }
}