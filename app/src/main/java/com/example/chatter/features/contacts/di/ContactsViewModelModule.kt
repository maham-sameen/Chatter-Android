package com.example.chatter.features.contacts.di


import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.core.domain.use_case.IsValidPhoneNo
import com.example.chatter.core.domain.use_case.ToHexCode
import com.example.chatter.features.contacts.domain.repository.ContactRepo
import com.example.chatter.features.contacts.domain.use_case.create_contact.CreateContactUseCase
import com.example.chatter.features.contacts.domain.use_case.GetAllContacts
import com.example.chatter.features.contacts.domain.use_case.create_chat.CreateChatUseCase
import com.example.chatter.features.contacts.domain.use_case.create_chat.GetAllContactsViaPhoneNo
import com.example.chatter.features.contacts.domain.use_case.create_chat.InsertChatRoom
import com.example.chatter.features.contacts.domain.use_case.create_contact.InsertContact
import com.example.chatter.features.contacts.domain.use_case.create_contact.IsUserExist
import com.example.chatter.features.contacts.presentation.contacts.ContactsViewModel
import com.example.chatter.features.contacts.presentation.create_chat.CreateChatViewModel
import com.example.chatter.features.contacts.presentation.create_contact.CreateContactViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope


@OptIn(KoinExperimentalAPI::class)
val contactsViewModelModule = module{
    viewModelOf(::CreateContactViewModel)
    viewModelOf(::ContactsViewModel)
    viewModelOf(::CreateChatViewModel)
    scope<CreateContactViewModel> {
        scoped<CreateContactUseCase>{
            CreateContactUseCase(
                insertContact = InsertContact(
                    contactRepo = get<ContactRepo>(),
                    isUserExist = IsUserExist(
                        contactRepo = get<ContactRepo>()
                    ),
                ),
                isValidPhoneNo = IsValidPhoneNo(),
            )
        }
    }
    viewModelScope {
        scoped<GetAllContacts>{
            GetAllContacts(
                contactRepo = get<ContactRepo>(),
                fromHexCode = get<FromHexCode>(),
            )
        }
    }

    viewModelScope{
        scoped<CreateChatUseCase> {
            CreateChatUseCase(
                getAllContactsViaPhoneNo = GetAllContactsViaPhoneNo(
                    contactRepo = get<ContactRepo>(),
                    fromHexCode = get<FromHexCode>()
                ),
                insertChatRoom = InsertChatRoom(
                    contactRepo = get<ContactRepo>(),
                    toHexCode = get<ToHexCode>(),
                    getRandomColor = get<GetRandomColor>()
                )
            )
        }
    }
}