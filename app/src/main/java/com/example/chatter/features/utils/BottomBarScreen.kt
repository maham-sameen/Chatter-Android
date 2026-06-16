package com.example.chatter.features.utils

import androidx.annotation.DrawableRes
import com.example.chatter.R
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomBarScreen (
    val title : String ,
    @DrawableRes
    val icon : Int  ,
    val hasNews : Boolean = false ,
    val badgeCount: Int? = null ,
    val route : String
){

    @Serializable
    object Chats : BottomBarScreen(
        title = "Chats",
        icon = R.drawable.chats_svgrepo_com,
        route = "chats"
    )

    @Serializable
    object Status : BottomBarScreen(
        title = "Status",
        icon = R.drawable.status_svgrepo_com,
        route = "status"
    )

    @Serializable
    object Contacts : BottomBarScreen(
        title = "Contacts",
        icon = R.drawable.contacts_svgrepo_com,
        route = "contacts"
    )

    @Serializable
    object Calls : BottomBarScreen(
        title = "Calls",
        icon = R.drawable.phone_calling_rounded_svgrepo_com ,
        route = "calls"
    )
}