package com.example.chatter.features.auth.presentation.utils

import com.example.chatter.core.presentation.model.PhoneNumber
import kotlinx.serialization.Serializable


@Serializable
sealed class AuthScreen{

    @Serializable
    object Intro : AuthScreen()

    @Serializable
    object Signup : AuthScreen()


    @Serializable
    object Signin : AuthScreen()

    @Serializable
    data class EnterCode(
        val username : String? = null ,
        val phoneNumber : PhoneNumber? = null
    ) : AuthScreen()
}
