package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.core.domain.use_case.FormateTime
import com.example.chatter.features.chats.presentation.conversation.model.ChatState
import com.example.chatter.features.chats.presentation.conversation.model.Member
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import org.koin.core.scope.Scope

class GetChatState(
    private val chatRepo: ChatRepo ,
    private val fromHexCode: FromHexCode ,
    private val formateTime: FormateTime ,
    private val getRandomColor: GetRandomColor ,
    private val sendMemberInfo: SendMemberInfo ,
    private val chatOpen: ChatOpen
) {

    /**
     * Here we getChat from the database and
     * also send the member info that user is online so other person knows
     * and also init session for the chat socket service .
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(roomId : String, scope: CoroutineScope): ChatState?{
        chatRepo.clearLastChat()
        println("\uD83E\uDD2F GetChatSTate : roomId : $roomId")
        val chat =  chatRepo.getConversationRoom(roomId = roomId)
            .onStart {
                sendMemberInfo(
                    roomId = roomId,
                    sendPresence = true,
                    isTyping = false
                )
            }
            .firstOrNull()  ?: return null
        if (chat.members.isEmpty()) {
            println("\uD83E\uDD2F GetChatSTate : members empty for roomId=$roomId, returning null")
            return null
        }
                println("\uD83E\uDD2F GetChatSTate : onSuccess : ${chat.toString()}")
                return chatRepo.initSession(
                    membersPhoneNumber = chat.members.map { it.phoneNumber.toString() },
                    scope = scope
                ).fold(
                    onSuccess = {
                        chatOpen.invoke(
                            roomId = chat.roomId
                        )
                        val member = if (chat.members.size == 1) {
                            chat.members[0]
                        } else {
                            null
                        }
                        val color = if (
                            member != null &&
                            member.colorScheme != null) {
                            member.colorScheme
                        } else if (chat.colorScheme != null &&
                            chat.colorScheme.background.isNotEmpty() &&
                            chat.colorScheme.icon.isNotEmpty()) {
                            chat.colorScheme
                        } else {
                            null
                        }
                        val lastSeen = "lastSeen at ${formateTime(member?.lastSeen ?: chat.timeStamp)}"
                        val memberNames = if (member != null) {
                            lastSeen
                        } else {
                            chat.members.joinToString(", ") { it.name }
                        }
                        ChatState(
                            name = member?.name ?: chat.name,
                            image = member?.image ?: chat.image,
                            lastSeen = lastSeen,
                            isOnline = false,
                            isTyping = false,
                            members = chat.members.map {
                                Member(
                                    phoneNumber = PhoneNumber(
                                        countryCode = it.phoneNumber.countryCode,
                                        phoneNumber = it.phoneNumber.phoneNumber
                                    ),
                                    name = it.name
                                )
                            },
                            membersNames = memberNames,
                            roomId = chat.roomId,
                            colorScheme = color?.let { fromHexCode(it) } ?: getRandomColor(),
                            presenceText = memberNames,
                        )
                    } ,
                    onFailure = {
                        println("\uD83E\uDD2F GetChatSTate :  onFailure : ${it.message.toString()}")
                        null
                    }
                )

    }
}