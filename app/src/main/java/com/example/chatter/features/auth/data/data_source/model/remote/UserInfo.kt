package com.example.chatter.features.auth.data.data_source.model.remote


import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val phoneNumber: PhoneNumber ,
    val image : String? = null ,
    val colorScheme: ColorScheme ,
    val lastSeen : Long  ,
    val name : String
)
