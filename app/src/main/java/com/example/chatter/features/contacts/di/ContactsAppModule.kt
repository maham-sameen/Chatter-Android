package com.example.chatter.features.contacts.di

import com.example.chatter.AppDatabase
import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.LocalImageStorage
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.remote.GetDownloadUrl
import com.example.chatter.core.data.data_source.remote.UserService
import com.example.chatter.core.data.data_source.remote.UsersDataStore
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import com.example.chatter.features.contacts.data.data_source.mapper.ChatRoomResponseToChatRoom
import com.example.chatter.features.contacts.data.data_source.mapper.ChatRoomToChatRoomRequest
import com.example.chatter.features.contacts.data.data_source.mapper.ChatRoomToDbs
import com.example.chatter.features.contacts.data.data_source.mapper.ContactEntityToContact
import com.example.chatter.features.contacts.data.data_source.mapper.ContactToContactEntity
import com.example.chatter.features.contacts.data.repository.ContactRepoImp
import com.example.chatter.features.contacts.domain.repository.ContactRepo
import io.ktor.client.HttpClient
import org.koin.dsl.module

val contactsAppModule = module {
    single<ContactDao> {
        get<AppDatabase>()
            .contactDao()
    }

    single<ContactRepo> {
        ContactRepoImp(
            contactDao = get<ContactDao>(),
            contactToContactEntity = ContactToContactEntity(),
            contactEntityToContact = ContactEntityToContact(),
            userDataSource = get<UserDataSource>(),
            client = get<HttpClient>(),
            localImageStorage = get<LocalImageStorage>(),
            getDownloadUrl = get<GetDownloadUrl>(),
            chatRoomToDbs = ChatRoomToDbs(),
            memberDao = get<MemberDao>(),
            chatRoomDao = get<ChatRoomDao>(),
            usersDataStore = get<UsersDataStore>(),
            chatRoomToChatRoomRequest = ChatRoomToChatRoomRequest(),
            chatRoomResponseToChatRoom = ChatRoomResponseToChatRoom(),
            appDatabase = get<AppDatabase>(),
            userService = get<UserService>()
        )
    }
}