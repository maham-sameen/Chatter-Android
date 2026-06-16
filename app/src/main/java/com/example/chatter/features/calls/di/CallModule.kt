package com.example.chatter.features.calls.di

import org.koin.dsl.module

val callModule = module {
    includes(callAppModule , callViewModelModule)
}