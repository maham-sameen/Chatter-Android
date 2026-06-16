package com.example.chatter.features.contacts.presentation.util

import kotlinx.serialization.Serializable

@Serializable
sealed class ContactScreen {

    @Serializable

    data object CreateContact : ContactScreen()

}


@Serializable
data class CreateChat(
    val contactsPhoneNumbers : String
) : ContactScreen()
