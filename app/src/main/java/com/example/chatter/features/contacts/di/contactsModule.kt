package com.example.chatter.features.contacts.di

import org.koin.dsl.module

val  contactsModule = module {
    includes(
        contactsAppModule ,
        contactsViewModelModule
    )
}