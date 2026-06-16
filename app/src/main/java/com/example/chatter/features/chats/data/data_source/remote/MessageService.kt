package com.example.chatter.features.chats.data.data_source.remote

import com.example.chatter.Constants.BASE_URL
import com.example.chatter.features.chats.data.data_source.model.remote.PaginatedRequest
import com.example.chatter.features.chats.data.data_source.model.remote.PaginatedResponse

interface MessageService {

    suspend fun getAllMessages(paginatedRequest: PaginatedRequest): Result<PaginatedResponse>

    sealed class EndPoints(val url : String){
        object GetMessagesPaged : EndPoints("$BASE_URL/messages")
    }
}