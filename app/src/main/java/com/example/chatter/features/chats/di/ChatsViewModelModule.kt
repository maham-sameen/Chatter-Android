package com.example.chatter.features.chats.di

import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.features.chats.domain.repository.ProfileRepo
import com.example.chatter.features.chats.domain.use_case.chats.ChatsUseCases
import com.example.chatter.core.domain.use_case.FormateTime
import com.example.chatter.features.chats.domain.use_case.chats.GetAllChats
import com.example.chatter.features.chats.domain.use_case.chats.IsSessionExpired
import com.example.chatter.features.chats.domain.use_case.conversation.ChatClose
import com.example.chatter.features.chats.domain.use_case.conversation.ConversationUseCases
import com.example.chatter.features.chats.domain.use_case.conversation.FormateMessageTimeStamp
import com.example.chatter.features.chats.domain.use_case.conversation.GetCallees
import com.example.chatter.features.chats.domain.use_case.conversation.GetChatState
import com.example.chatter.features.chats.domain.use_case.conversation.GetMessages
import com.example.chatter.features.chats.domain.use_case.conversation.GetSenderName
import com.example.chatter.features.chats.domain.use_case.conversation.ObserveMemberInfos
import com.example.chatter.features.chats.domain.use_case.conversation.ObserveMessage
import com.example.chatter.features.chats.domain.use_case.conversation.SendMemberInfo
import com.example.chatter.features.chats.domain.use_case.conversation.SendMessage
import com.example.chatter.features.chats.domain.use_case.conversation.SendClosedChatMsg
import com.example.chatter.features.chats.domain.use_case.chats.UpdateChatRoom
import com.example.chatter.features.chats.domain.use_case.conversation.ChatOpen
import com.example.chatter.features.chats.domain.use_case.conversation.InitChatSession
import com.example.chatter.features.chats.domain.use_case.conversation.MarkMessagesAsRead
import com.example.chatter.features.chats.domain.use_case.conversation.SaveIncomingMessage
import com.example.chatter.features.chats.domain.use_case.conversation.UpdateContactLastSeen
import com.example.chatter.features.chats.domain.use_case.profile.GetDefaultUser
import com.example.chatter.features.chats.domain.use_case.profile.GetUser
import com.example.chatter.features.chats.domain.use_case.profile.ProfileUseCases
import com.example.chatter.features.chats.domain.use_case.profile.UpdateUserProfile
import com.example.chatter.features.chats.presentation.chats.ChatsViewModel
import com.example.chatter.features.chats.presentation.conversation.ConversationViewModel
import com.example.chatter.features.chats.presentation.profile.ProfileViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


@OptIn(KoinExperimentalAPI::class)
val chatsViewModelModule = module {
    scope<ProfileViewModel> {
        scoped<ProfileUseCases> {
            ProfileUseCases(
                getUser = GetUser(
                    get<ProfileRepo>(),
                    fromHexCode = get<FromHexCode>(),
                    getRandomColor = get<GetRandomColor>()
                ),
                getDefaultUser = GetDefaultUser(get<GetRandomColor>()),
                updateUserProfile = UpdateUserProfile(get<ProfileRepo>()),
            )
        }
    }

    scope<ChatsViewModel> {
        scoped<ChatsUseCases> {
            ChatsUseCases(
                isSessionExpired = IsSessionExpired(get<ChatRepo>()),
                getAllChats = GetAllChats(
                    chatsRepo = get<ChatRepo>(),
                    fromHexCode = get<FromHexCode>(),
                    generateRandomColor = get<GetRandomColor>(),
                    formateTime = get<FormateTime>()
                ) ,
                updateChatRoom = UpdateChatRoom(
                    chatRepo = get<ChatRepo>()
                ),
            )
        }
    }
    scope<ConversationViewModel>{
        val getSenderName = GetSenderName()
        scoped<ConversationUseCases> {
            ConversationUseCases(
                getChatState = GetChatState(
                    chatRepo = get<ChatRepo>(),
                    fromHexCode = get<FromHexCode>(),
                    formateTime = get<FormateTime>(),
                    getRandomColor = get<GetRandomColor>(),
                    sendMemberInfo = SendMemberInfo(get<ChatRepo>()),
                    chatOpen = ChatOpen(
                        chatRepo = get<ChatRepo>()
                    ),
                ),
                observeMemberInfos = ObserveMemberInfos(
                    chatRepo = get<ChatRepo>()
                ),
                sendMemberInfo = SendMemberInfo(
                    chatRepo = get<ChatRepo>()
                ),
                chatClose = ChatClose(
                    chatRepo = get<ChatRepo>()
                ),
                sendMessage = SendMessage(
                    chatRepo = get<ChatRepo>()
                ),
                getMessages = GetMessages(
                    chatRepo = get<ChatRepo>(),
                    getSenderName = getSenderName,
                    formateMessageTimeStamp = FormateMessageTimeStamp(),
                ),
                observeMessage = ObserveMessage(
                    chatRepo = get<ChatRepo>(),
                ),
                sendClosedChatMsg = SendClosedChatMsg(
                    chatRepo = get<ChatRepo>()
                ),
                getCallees = GetCallees(
                    chatRepo = get<ChatRepo>()
                ),
                markMessagesAsRead = MarkMessagesAsRead(
                    chatRepo = get<ChatRepo>()
                ),
                chatOpen = ChatOpen(
                    chatRepo = get<ChatRepo>()
                ),
                initChatSession = InitChatSession(
                    chatRepo = get<ChatRepo>()
                ),
                updateContactLastSeen = UpdateContactLastSeen(
                    chatRepo = get<ChatRepo>()
                ),
                saveIncomingMessage = SaveIncomingMessage(
                    chatRepo = get<ChatRepo>()
                ),
                formateTime = get<FormateTime>(),
            )
        }
    }
    viewModelOf(::ChatsViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ConversationViewModel)
}