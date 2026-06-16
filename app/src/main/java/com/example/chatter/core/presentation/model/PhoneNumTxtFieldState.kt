package com.example.chatter.core.presentation.model

import com.stevdza_san.library.domain.Country

data class PhoneNumTxtFieldState(
    val country : Country = Country.Pakistan,
    val phoneNumber : FocusedErrorTxtFieldState = FocusedErrorTxtFieldState()
)