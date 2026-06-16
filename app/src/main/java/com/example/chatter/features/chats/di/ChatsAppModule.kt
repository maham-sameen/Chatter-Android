package com.example.chatter.features.chats.di


import com.example.chatter.AppDatabase
import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.features.chats.data.data_source.local.ImageStorageWorker
import com.example.chatter.core.data.data_source.local.LocalImageStorage
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.MessageDao
import com.example.chatter.core.data.data_source.remote.ChatterSocket
import com.example.chatter.core.data.data_source.remote.GetDownloadUrl
import com.example.chatter.core.data.data_source.remote.UserService
import com.example.chatter.features.chats.data.data_source.local.MessageRemoteKeyDao
import com.example.chatter.features.chats.data.data_source.local.UpdateUnreadMessagesService
import com.example.chatter.features.chats.data.data_source.local.UpdateUnreadMessagesWorker
import com.example.chatter.features.chats.data.data_source.mapper.ContactEntityToUser
import com.example.chatter.features.chats.data.data_source.mapper.MessageEntityToMessage
import com.example.chatter.features.chats.data.data_source.mapper.MessageToMessageEntity
import com.example.chatter.features.chats.data.data_source.mapper.PresenceDbToPresence
import com.example.chatter.features.chats.data.data_source.mapper.PresenceToPresenceDb
import com.example.chatter.features.chats.data.data_source.mapper.TypingDbToTyping
import com.example.chatter.features.chats.data.data_source.mapper.TypingToTypingDb
import com.example.chatter.features.chats.data.data_source.mapper.UserDbToUser
import com.example.chatter.features.chats.data.data_source.mapper.ChatOpenedDbToChatOpened
import com.example.chatter.features.chats.data.data_source.remote.ChatSocketService
import com.example.chatter.features.chats.data.data_source.remote.ChatSocketServiceImp
import com.example.chatter.features.chats.data.data_source.remote.MessageService
import com.example.chatter.features.chats.data.data_source.remote.MessageServiceImp
import com.example.chatter.features.chats.data.data_source.remote.RemoteImageStorage
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketService
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketServiceImp
import com.example.chatter.features.chats.data.repository.ChatRepoImp
import com.example.chatter.features.chats.data.repository.ProfileRepoImp
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.features.chats.domain.repository.ProfileRepo
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module



val chatsAppModule = module {
    workerOf(::ImageStorageWorker)
    workerOf(::UpdateUnreadMessagesWorker)
    single<ProfileRepo> {
        ProfileRepoImp(
            userDataSource = get<UserDataSource>(),
            userDbToUser = UserDbToUser(),
            localImageStorage = LocalImageStorage(androidContext()),
            remoteImageStorage = RemoteImageStorage(
                client = get<HttpClient>(),
                localImageStorage = LocalImageStorage(androidContext()),
                geetDownloadUrl = GetDownloadUrl()
            )
        )
    }

    single<PresenceSocketService> {
        PresenceSocketServiceImp(
            client = get<HttpClient>(),
            presenceDbToPresence = PresenceDbToPresence(),
            typingDbToTyping = TypingDbToTyping(),
            presenceToPresenceDb = PresenceToPresenceDb(),
            typingToTypingDb = TypingToTypingDb(),
        )
    }


    single<ChatSocketService> {
        ChatSocketServiceImp(
            client = get<HttpClient>(),
            chatOpenedDbToChatOpened = ChatOpenedDbToChatOpened()
        )
    }


    single<MessageService>{
        MessageServiceImp(
            client = get<HttpClient>()
        )
    }

    single<MessageDao>{
        get<AppDatabase>().messageDao()
    }

    single<MessageRemoteKeyDao>{
        get<AppDatabase>().remoteKeyDao()
    }

    single {
        UpdateUnreadMessagesService(
            chatRoomDao = get<ChatRoomDao>(),
            client = get<HttpClient>(),
            userDataSource = get<UserDataSource>(),
            memberDao = get<MemberDao>(),
            contactDao = get<ContactDao>(),
            appDatabase = get<AppDatabase>()
        )
    }

    single<ChatRepo>{
        ChatRepoImp(
            userDataSource = get<UserDataSource>(),
            client = get<HttpClient>(),
            presenceSocketService = get<PresenceSocketService>(),
            chatRoomDao = get<ChatRoomDao>(),
            contactDao = get<ContactDao>(),
            memberDao = get<MemberDao>(),
            contactEntityToUser = ContactEntityToUser(),
            chatSocketService = get<ChatSocketService>(),
            messageDao = get<MessageDao>(),
            messageRemoteKeyDao = get<MessageRemoteKeyDao>(),
            messageToMessageEntity = MessageToMessageEntity(),
            messageService = get<MessageService>(),
            messageEntityToMessage = MessageEntityToMessage(),
            updateUnreadMessagesService = get<UpdateUnreadMessagesService>(),
            userService = get<UserService>(),
            appDatabase = get<AppDatabase>(),
        )
    }
}