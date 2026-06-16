package com.example.chatter.core.data.data_source.model.local

import kotlinx.serialization.Serializable

@Serializable
data class ColorScheme(
    val background : String ,
    val icon : String
)