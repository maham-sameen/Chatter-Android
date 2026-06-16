package com.example.chatter.features.auth.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable

/**
 * that will server response to the users
 */
@Serializable
data class AuthResponse(
    val token : String ,
    val name  : String ,
    val phoneNumber: PhoneNumber ,
    val colorScheme: ColorScheme ,
    val  image : String? = null
)