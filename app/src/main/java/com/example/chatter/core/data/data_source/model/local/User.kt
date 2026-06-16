package com.example.chatter.core.data.data_source.model.local

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name : String,
    val phoneNumber: PhoneNumber,
    val colorScheme: ColorScheme,
    val image : String? = null,
)