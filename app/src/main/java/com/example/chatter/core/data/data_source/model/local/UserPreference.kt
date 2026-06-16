package com.example.chatter.core.data.data_source.model.local

import kotlinx.serialization.Serializable

@Serializable
data class UserPreference(
    val user: User? = null,
    val userToken: String = "",
)