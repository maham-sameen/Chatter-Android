package com.example.chatter.features.auth.domain.model

import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber


data class CheckCodeSignup(
    val phoneNumber: PhoneNumber,
    val code : String,
    val colorScheme: ColorScheme
)


