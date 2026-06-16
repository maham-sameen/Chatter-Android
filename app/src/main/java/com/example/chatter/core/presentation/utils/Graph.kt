package com.example.chatter.core.presentation.utils

import kotlinx.serialization.Serializable

sealed class Graph {
    @Serializable
    data object Authentication : Graph()

    @Serializable
    data object Home : Graph()

    @Serializable
    data object Settings : Graph()


    @Serializable
    data object Contacts : Graph()


    @Serializable
    data class Conversation(
        val roomId : String ,
    ) : Graph()
}