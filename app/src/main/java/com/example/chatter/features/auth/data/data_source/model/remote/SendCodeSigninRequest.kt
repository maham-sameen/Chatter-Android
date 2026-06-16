package com.example.chatter.features.auth.data.data_source.model.remote

import kotlinx.serialization.Serializable


@Serializable
data class SendCodeSigninRequest(
    val username : String ,
    val password : String ,
)
