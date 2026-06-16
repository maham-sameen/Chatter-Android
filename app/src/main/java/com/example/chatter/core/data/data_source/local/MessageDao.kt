package com.example.chatter.core.data.data_source.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.chatter.features.chats.data.data_source.model.local.MessageEntity

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE roomId = :q ORDER BY timeStamp DESC")
    fun getMessages(q : String) : PagingSource<Int , MessageEntity>


    @Query("DELETE FROM messages")
    suspend fun clear()

    @Upsert
    suspend fun upsert(entity: MessageEntity)

    @Query("""
    UPDATE messages
    SET isRead = :isRead
    WHERE roomId = :roomId
""")
    suspend fun markMessagesAsRead(
        roomId: String,
        isRead: Boolean = true
    )
}