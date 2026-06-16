package com.example.chatter.features.contacts.domain.use_case.create_contact

import com.example.chatter.core.domain.use_case.IsValidPhoneNo

data class CreateContactUseCase(
    val insertContact: InsertContact,
    val isValidPhoneNo: IsValidPhoneNo ,
)