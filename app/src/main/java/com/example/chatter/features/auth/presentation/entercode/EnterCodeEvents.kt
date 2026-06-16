package com.example.chatter.features.auth.presentation.entercode

import androidx.compose.ui.focus.FocusState
import com.example.chatter.core.presentation.model.PhoneNumber

sealed class EnterCodeEvents  {

    data class EnterCode(val text : String) : EnterCodeEvents()
    data class Verify(
        val phoneNumber : PhoneNumber? = null ,
        val username : String? = null
    ) : EnterCodeEvents()
    object Resend : EnterCodeEvents()
    data class CodeFocusChanged(val focusState: FocusState) : EnterCodeEvents()
    object ClearUIState : EnterCodeEvents()
}