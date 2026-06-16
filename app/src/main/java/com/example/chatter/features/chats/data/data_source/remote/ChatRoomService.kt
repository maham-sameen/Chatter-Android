package com.example.chatter.features.chats.data.data_source.remote

import com.example.chatter.Constants

interface ChatRoomService {
    sealed class EndPoints(val url : String){
        object GetChatRooms : EndPoints("${Constants.BASE_URL}/get_chat_rooms")
        object GetConversationRooms : EndPoints("${Constants.BASE_URL}/get_conversation_room")
        object GetUpdatedChatRoom : EndPoints("${Constants.BASE_URL}/get_updated_chat_room")
    }
}