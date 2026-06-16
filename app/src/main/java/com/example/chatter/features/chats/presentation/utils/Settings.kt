package com.example.chatter.features.chats.presentation.utils

import kotlinx.serialization.Serializable

sealed class Settings {
    @Serializable
    data object Profile : Settings()

}