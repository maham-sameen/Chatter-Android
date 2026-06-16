package com.example.chatter.features.chats.data.data_source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatter.core.data.data_source.local.BaseDao
import com.example.chatter.features.chats.data.data_source.model.local.MessageRemoteKey

@Dao
interface MessageRemoteKeyDao  {

    @Query("SELECT * FROM messages_remote_keys WHERE id = :id")
    suspend fun getRemoteKey(id : String): MessageRemoteKey?

    @Query("DELETE FROM messages_remote_keys WHERE roomId = :roomId")
    suspend fun deleteRemoteKeyRoomId(roomId : String)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(list: List<MessageRemoteKey>)
}