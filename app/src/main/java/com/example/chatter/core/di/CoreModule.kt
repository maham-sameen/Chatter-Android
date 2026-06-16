package com.example.chatter.core.di

import org.koin.dsl.module

val coreModule = module {
    includes(coreAppModule , coreViewModelModule)
}