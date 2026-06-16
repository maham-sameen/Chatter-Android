package com.example.chatter.features.contacts.presentation.create_contact

import androidx.compose.ui.focus.FocusState
import com.example.chatter.features.auth.presentation.signin.SigninEvents
import com.stevdza_san.library.domain.Country

sealed  class CreateContactEvent {
    data class EnterName(val text : String) : CreateContactEvent()
    data class EnterPhoneNo(val text : String) : CreateContactEvent()
    data class ChangeCountry(val country : Country) : CreateContactEvent()
    data class NameFocusChange(
        val focusState : FocusState
    ) : CreateContactEvent()
    data class PhoneNoFocusChange(
        val focusState : FocusState
    ) : CreateContactEvent()
    object SaveAndContinue : CreateContactEvent()
    object ClearUIState : CreateContactEvent()

}