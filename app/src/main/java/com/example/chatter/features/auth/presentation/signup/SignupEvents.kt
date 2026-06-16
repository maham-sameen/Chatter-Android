package com.example.chatter.features.auth.presentation.signup

import androidx.compose.ui.focus.FocusState
import com.stevdza_san.library.domain.Country

sealed class SignupEvents {
    data class EnterUsername(val text : String) : SignupEvents()
    data class UsernameFocusChanged(val focusState: FocusState) : SignupEvents()
    data class PasswordFocusChanged(val focusState: FocusState) : SignupEvents()
    data class EnterPassword(val text : String) : SignupEvents()
    data class PhoneNumberFocusChanged(val focusState: FocusState) : SignupEvents()
    data class EnterPhoneNumber(val text : String) : SignupEvents()
    object Signup : SignupEvents()
    data class DialogStateChanged(val state : Boolean) : SignupEvents()
    data class CountryChanged(val country : Country) : SignupEvents()
    object ClearUIState : SignupEvents()
    data class EnterEmail(val text : String) : SignupEvents()
    data class EmailFocusChanged(val focusState: FocusState) : SignupEvents()
}