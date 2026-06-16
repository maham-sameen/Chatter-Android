package com.example.chatter.features.contacts.presentation.contacts

sealed class ContactsEvent {

    data class OnToggleSelection(val index : Int) : ContactsEvent()

    data object Action : ContactsEvent()

    data object CreateAChat : ContactsEvent()

    data object ClearUIState : ContactsEvent()
}