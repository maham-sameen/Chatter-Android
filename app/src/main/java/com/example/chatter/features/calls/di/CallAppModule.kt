package com.example.chatter.features.calls.di

import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.features.calls.data.data_source.mapper.ChatterCallDbToChatterCall
import com.example.chatter.features.calls.data.data_source.remote.CallDevice
import com.example.chatter.features.calls.data.data_source.remote.CallRemoteDataSource
import com.example.chatter.features.calls.data.data_source.remote.CallService
import com.example.chatter.features.calls.data.data_source.remote.CallServiceImp
import com.example.chatter.features.calls.data.data_source.remote.UserService
import com.example.chatter.features.calls.data.data_source.remote.UserServiceImp
import com.example.chatter.features.calls.data.repository.CallRepoImp
import com.example.chatter.features.calls.data.utils.ChatterVideoInitHelper
import com.example.chatter.features.calls.domain.repository.CallRepo
import com.example.chatter.features.chats.data.data_source.remote.ChatSocketService
import com.example.chatter.features.notifications.data.data_source.remote.CustomNotificationHandler
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val callAppModule = module {
    single<UserService> {
        UserServiceImp(
            client = get<HttpClient>()
        )
    }

    single<CallDevice> {
        CallDevice()
    }

    single<CustomNotificationHandler>{
        CustomNotificationHandler(
            application = androidApplication(),
        )
    }

    single<CallService>{
        CallServiceImp()
    }

    single<ChatterVideoInitHelper>{
        ChatterVideoInitHelper(
            userService = get<UserService>(),
            userDataSource = get<UserDataSource>(),
            callDevice = get<CallDevice>(),
            context = androidApplication()
        )
    }

    single<CallRemoteDataSource>{
        CallRemoteDataSource()
    }

    single<CallRepo>{
        CallRepoImp(
            callRemoteDataSource = get<CallRemoteDataSource>(),
            chatterCallDbToChatterCall = ChatterCallDbToChatterCall(),
            chatSocketService = get<ChatSocketService>()
        )
    }
}