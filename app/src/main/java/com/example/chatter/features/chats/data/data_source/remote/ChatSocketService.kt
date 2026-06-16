package com.example.chatter.features.chats.data.data_source.remote

import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.features.chats.domain.model.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface ChatSocketService {
    suspend fun initSession(userSession: UserSession , scope : CoroutineScope): Result<Unit>
    suspend fun closeSession()
    suspend fun sendMessage(json : String)
    fun observerMessage(): SharedFlow<Any?>
    fun clearLastChat()
    companion object{
        const val url = "ws://192.168.1.50:8083"
    }
    sealed class EndPoints(val url : String){
        object ChatSocket : EndPoints("$url/chat-socket")
    }
}