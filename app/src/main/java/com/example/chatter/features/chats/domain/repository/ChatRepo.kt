package com.example.chatter.features.chats.domain.repository

import androidx.paging.PagingData
import androidx.work.WorkManager
import com.example.chatter.features.chats.domain.model.ChatRoom
import com.example.chatter.features.chats.domain.model.ConversationRoom
import com.example.chatter.features.chats.domain.model.Message
import com.example.chatter.features.chats.domain.model.ClosedChatMsg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface ChatRepo {

    fun getAllChatRooms(): Flow<List<ChatRoom>>

    suspend fun isTokenValid(): Boolean

     fun getConversationRoom(roomId : String) : Flow<ConversationRoom>

    suspend fun sendMemberInfo(memberInfo: Any)
    fun observerMemberInfo(): SharedFlow<Any?>

    suspend fun getUserPhoneNumber(): String

    suspend fun updateContactLastSeen(lastSeen : Long , phoneNumber : String , countryCode : String) : Int

    suspend fun initSession(membersPhoneNumber : List<String> , scope : CoroutineScope): Result<Unit>
    suspend fun closeSession()
    suspend fun sendMessage(message : String)

    fun getMessageRemoteMediator(roomId : String , phoneNumbers: List<com.example.chatter.core.domain.model.PhoneNumber>) : Flow<PagingData<Message>>

    fun observeMessage(): SharedFlow<Any?>

    suspend fun saveMessage(message : Message , roomId : String)

    suspend fun sendClosedChatMsg(
        closedChatMsg: ClosedChatMsg
    )


    suspend fun markMessagesAsRead(
        roomId : String
    )

    suspend fun updateChatRoom(
        roomId : String
    ) : Result<Unit>

    fun uploadDataToWorker(
        roomId : String ,
        timeStamp : Long ,
        lastMessage : String ,
        workManager: WorkManager
    )


    suspend fun updateUnreadMessages(
        roomId : String ,
        timeStamp : Long ,
        lastMessage : String ,
    )


    suspend fun sendOpenedChatMsg(
        openChatMsg : OpenedChatMsg
    )

    fun clearLastChat()
}