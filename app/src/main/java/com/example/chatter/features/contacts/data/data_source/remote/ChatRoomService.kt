package com.example.chatter.features.contacts.data.data_source.remote

import com.example.chatter.Constants

interface ChatRoomService {

    sealed class EndPoints(val url : String){
        object SaveChatRoom : EndPoints(
            url = "${Constants.BASE_URL}/save_chat_room"
        )
    }
}