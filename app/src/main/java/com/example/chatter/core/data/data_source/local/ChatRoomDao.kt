package com.example.chatter.core.data.data_source.local

import androidx.room.Dao
import androidx.room.Query
import com.example.chatter.core.data.data_source.model.local.ChatRoomEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ChatRoomDao : BaseDao<ChatRoomEntity>{

    @Query("DELETE FROM chats")
    suspend fun clear()


    @Query("DELETE  FROM chats WHERE roomId = :id")
    suspend fun deleteChatRoomByRoomId(id : String)

    @Query("SELECT * FROM chats ORDER BY timeStamp DESC")
    fun getAllChats(): Flow<List<ChatRoomEntity>>

    @Query("SELECT * FROM chats WHERE roomId = :id")
    fun getChatRoomById(id : String) : Flow<List<ChatRoomEntity>>

    @Query("""
    UPDATE chats
    SET lastMessage = :lastMessage,
        timeStamp = :timeStamp,
        unreadMessages = :unreadMessages
    WHERE roomId = :roomId
""")
    suspend fun updateChatRoom(
        roomId: String,
        lastMessage: String,
        timeStamp: Long,
        unreadMessages: Int
    )

    @Query("""
    UPDATE chats 
    SET lastMessage = :lastMessage, timeStamp = :timeStamp
    WHERE roomId = :roomId
""")
    suspend fun updateLastMessageOnly(
        roomId: String,
        lastMessage: String,
        timeStamp: Long
    )

    @Query("""
    UPDATE chats
    SET unreadMessages = :unreadMessages
    WHERE roomId = :roomId
""")
    suspend fun updateChatRoomUnreadMessages(
        roomId: String,
        unreadMessages: Int
    )

}