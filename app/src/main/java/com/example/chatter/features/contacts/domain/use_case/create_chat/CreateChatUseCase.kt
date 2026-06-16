package com.example.chatter.features.contacts.domain.use_case.create_chat


data class CreateChatUseCase(
    val getAllContactsViaPhoneNo: GetAllContactsViaPhoneNo ,
    val insertChatRoom: InsertChatRoom
)