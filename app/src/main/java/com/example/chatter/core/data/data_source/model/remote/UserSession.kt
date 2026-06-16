package com.example.chatter.core.data.data_source.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val userPhone : String,
    val contactPhone : List<String>,
)