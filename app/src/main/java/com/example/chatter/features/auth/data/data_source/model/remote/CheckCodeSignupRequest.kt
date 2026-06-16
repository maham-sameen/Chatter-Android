package com.example.chatter.features.auth.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable

@Serializable
data class CheckCodeSignupRequest(
    val phoneNumber: PhoneNumber ,
    val code : String ,
    val colorScheme: ColorScheme
)
