package com.example.chatter.features.chats.di

import org.koin.dsl.module

val chatsModule = module {
    includes(chatsAppModule , chatsViewModelModule)
}