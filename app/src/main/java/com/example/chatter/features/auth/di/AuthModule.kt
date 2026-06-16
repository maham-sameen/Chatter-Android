package com.example.chatter.features.auth.di

import org.koin.dsl.module

val authModule = module {
    includes(authViewModelModule , authAppModule)
}