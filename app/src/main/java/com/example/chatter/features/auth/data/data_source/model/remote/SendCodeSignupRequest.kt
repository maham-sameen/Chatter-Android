package com.example.chatter.features.auth.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable

/**
 * It is user representation of what the user provides
 * once they register and log in
 */

@Serializable
data class SendCodeSignupRequest(
    val username : String ,
    val password : String ,
    val phoneNumber : PhoneNumber ,
    val email : String
)