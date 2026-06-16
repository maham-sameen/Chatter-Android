package com.example.chatter.features.contacts.domain.repository

import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.core.domain.model.User
import com.example.chatter.features.contacts.domain.model.ChatRoom
import com.example.chatter.features.contacts.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepo {

    suspend fun insertContact(
        contact : Contact
    ) : Boolean

    suspend fun getCurrentUserId() : String

   fun getAllContacts() : Flow<List<Contact>>

    suspend fun getFullContact(contact: Contact) : Result<Contact>

    suspend fun getContactViaPhoneNumber(
        countryCode : String ,
        phoneNumber : String
    ) : Contact?

    suspend fun uploadChatRoomImage(
        image : ByteArray
    ) : Result<String>

    suspend fun insertChatRoom(
        chatRoom: ChatRoom ,
        phoneNumber: PhoneNumber
    ): Result<Unit>

    suspend fun getAllUsers() : Result<List<User>>

    suspend fun saveChatRoom(
        chatRoom: ChatRoom
    ): Result<ChatRoom>

    suspend fun updateLastSeenByPhoneNumber(
        lastSeen  : Long ,
        phoneNumber : String ,
        countryCode : String
    )
}