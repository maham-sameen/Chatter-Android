package com.example.chatter.features.auth.di

import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.features.auth.data.data_source.model.mapper.CCSigninToCCSigninReq
import com.example.chatter.features.auth.data.data_source.model.mapper.CCSignupToCCSignupReq
import com.example.chatter.features.auth.data.data_source.model.mapper.SCSigninToSCSigninReq
import com.example.chatter.features.auth.data.data_source.model.mapper.SCSignupToSCSignupReq
import com.example.chatter.core.data.data_source.remote.UsersDataStore
import com.example.chatter.features.auth.data.data_source.model.mapper.ChatRoomResponseToChatRoomEntity
import com.example.chatter.features.auth.data.data_source.model.remote.AuthResponseToUserPreference
import com.example.chatter.features.auth.data.repository.AuthRepoImp
import com.example.chatter.features.auth.domain.repository.AuthRepo
import com.example.chatter.features.calls.data.data_source.remote.CallDevice
import com.example.chatter.features.calls.data.data_source.remote.UserService
import com.example.chatter.features.calls.data.utils.ChatterVideoInitHelper
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketService
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import com.example.chatter.features.notifications.data.data_source.remote.CustomNotificationHandler
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val authAppModule = module {
    single<AuthRepo> {
        AuthRepoImp(
            client = get<HttpClient>(),
            scSignupToSCSignupReq = SCSignupToSCSignupReq(),
            ccSignupToCCSignupReq = CCSignupToCCSignupReq(),
            scSigninToSCSigninReq = SCSigninToSCSigninReq(),
            ccSigninToCCSigninReq = CCSigninToCCSigninReq(),
            userDataSource = get<UserDataSource>(),
            authResponseToUserPreference = AuthResponseToUserPreference(),
            usersDataStore = get<UsersDataStore>(),
            chatRoomDao = get<ChatRoomDao>(),
            memberDao = get<MemberDao>(),
            contactDao = get<ContactDao>(),
            chatRoomResponseToChatRoomEntity = ChatRoomResponseToChatRoomEntity(),
            chatterVideoInitHelper = get<ChatterVideoInitHelper>(),
            userSocketService = get<PresenceSocketService>()
        )
    }
}