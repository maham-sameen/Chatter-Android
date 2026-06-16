package com.example.chatter.features.auth.presentation.signin

import androidx.compose.ui.focus.FocusState

sealed class SigninEvents {
    data class EnterUsername(val text : String) : SigninEvents()
    data class UsernameFocusChanged(val focusState: FocusState) : SigninEvents()
    data class PasswordFocusChanged(val focusState: FocusState) : SigninEvents()
    data class EnterPassword(val text : String) : SigninEvents()
    object Signin : SigninEvents()
    object ClearUIState : SigninEvents()
}