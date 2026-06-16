package com.example.chatter.features.chats.data.data_source.remote

import com.example.chatter.core.data.data_source.model.remote.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface PresenceSocketService {
    suspend fun initSession(userSession: UserSession , scope : CoroutineScope): Result<Unit>
    suspend fun closeSession()
    fun observerMemberInfo(): SharedFlow<Any?>
    suspend fun sendMemberInfo(memberInfo : Any)

    companion object{
        const val url = "ws://192.168.1.50:8083"
    }
    sealed class EndPoints(val url : String){
        object PresenceSocket : EndPoints("$url/presence-socket")
    }
}