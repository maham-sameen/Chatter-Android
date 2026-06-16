package com.example.chatter

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.MessageDao
import com.example.chatter.core.data.data_source.model.local.ChatRoomEntity
import com.example.chatter.core.data.data_source.model.local.MemberEntity
import com.example.chatter.features.calls.data.data_source.model.local.Converters
import com.example.chatter.features.chats.data.data_source.local.MessageRemoteKeyDao
import com.example.chatter.features.chats.data.data_source.model.local.MessageEntity
import com.example.chatter.features.chats.data.data_source.model.local.MessageRemoteKey
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity


@Database(
    entities = [
        ContactEntity::class ,
        ChatRoomEntity::class ,
        MessageEntity :: class ,
        MemberEntity::class ,
        MessageRemoteKey::class
    ],
    version = 1 ,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase  : RoomDatabase(){
    abstract fun contactDao() : ContactDao
    abstract fun chatRoomDao() : ChatRoomDao
    abstract fun messageDao() : MessageDao
    abstract fun memberDao() : MemberDao
    abstract fun remoteKeyDao() : MessageRemoteKeyDao
}