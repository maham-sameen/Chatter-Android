package com.example.chatter.features.chats.domain.use_case.conversation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.PagingData
import androidx.paging.map
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.features.chats.presentation.conversation.model.Member
import com.example.chatter.features.chats.presentation.conversation.model.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest



class GetMessages(
    private val chatRepo: ChatRepo,
    private val formateMessageTimeStamp: FormateMessageTimeStamp ,
    private val getSenderName: GetSenderName,
) {

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        roomId: String,
        members: List<Member>
    ): Flow<PagingData<Message>> {
        println("\uD83D\uDC24 GetMessages() , roomId : $roomId")
        return chatRepo
            .getMessageRemoteMediator(
                roomId,
                phoneNumbers = members.map {
                    println("\uD83D\uDC24 GetMessages() , roomId : $roomId , phoneNumber $it ")
                    PhoneNumber(
                        countryCode = it.phoneNumber.countryCode,
                        phoneNumber = it.phoneNumber.phoneNumber
                    )
                }
            )
            .mapLatest { pagingData ->
                pagingData.map { entity ->
                    println("\uD83C\uDFF9  GetMessages : entity : ${entity.toString()}")
                    val (date , time) = formateMessageTimeStamp(entity.timeStamp)
                    Message(
                        text = entity.text,
                        formattedTime = time,
                        username = getSenderName(members, entity.phoneNumber),
                        id = entity.id,
                        status = entity.isRead,
                        dateGroup = date
                    )
                }
            }
    }

}

