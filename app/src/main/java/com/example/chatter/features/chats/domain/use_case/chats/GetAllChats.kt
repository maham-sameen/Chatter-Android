package com.example.chatter.features.chats.domain.use_case.chats

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.chatter.core.domain.use_case.FormateTime
import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.features.chats.presentation.chats.ChatRoom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllChats (
    private val chatsRepo: ChatRepo ,
    private val fromHexCode: FromHexCode ,
    private val generateRandomColor: GetRandomColor ,
    private val formateTime: FormateTime
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<SnapshotStateList<ChatRoom>>{
        println("\uD83E\uDD76 getAllChats")
        return chatsRepo.getAllChatRooms()
            .mapLatest {chats ->
                println("\uD83E\uDD76 getAllChats , chats : ${chats.toString()}")
               val list = mutableStateListOf<ChatRoom>()
                list.addAll(chats.map { chat ->
                    val color = if(chat.colorScheme == null
                        || (chat.colorScheme.icon.isEmpty()
                                && chat.colorScheme.background.isEmpty())){
                        generateRandomColor()
                    }else{
                        fromHexCode(chat.colorScheme)
                    }
                    ChatRoom(
                        roomId = chat.roomId,
                        profilePath = chat.image,
                        name = chat.name,
                        timeStamp = formateTime(chat.timeStamp),
                        lastMessage = chat.lastMessage,
                        color = color,
                        unreadMessages = chat.unreadMessages,
                    )
                })
                list
            }
    }
}