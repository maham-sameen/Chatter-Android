package com.example.chatter.features.calls.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val phoneNumber: PhoneNumber,
)
