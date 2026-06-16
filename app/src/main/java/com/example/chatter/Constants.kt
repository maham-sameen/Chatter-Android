package com.example.chatter

import com.example.chatter.features.utils.BottomBarScreen


object Constants {

    const val BASE_URL = "http://192.168.1.50:8083"

    val screenRoutes = listOf(
        BottomBarScreen.Chats.route,
        BottomBarScreen.Calls.route,
        BottomBarScreen.Contacts.route,
        BottomBarScreen.Status.route
    )

    const val MAIN_CHANNEL_ID = "MAIN_CHANNEL_ID"

    const val MAIN_CHANNEL = "MAIN_CHANNEL"


    const val PUSH_PROVIDER_NAME = "chatter_firebase"

    const val API_KEY = "9zjrxu4rqnxs"
}

