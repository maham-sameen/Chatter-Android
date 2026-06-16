package com.example.chatter.features.chats.presentation.profile.model

import androidx.annotation.DrawableRes


data class UserDetail(
    val name : String ,
    @DrawableRes
    val icon : Int ,
    val value : String
)