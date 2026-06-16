package com.example.chatter.features.calls.presentation.call_history.model

import androidx.annotation.DrawableRes
import com.example.chatter.ui.ColorScheme

data class Call(
    val callId: String,
    val members: List<String>,
    val image : String? = null,
    val colorScheme: ColorScheme,
    val name : String,
    val status : String,
    val isVideo : Boolean,
    val timeStamp : String ,
    @DrawableRes
    val statusIcon : Int
)
