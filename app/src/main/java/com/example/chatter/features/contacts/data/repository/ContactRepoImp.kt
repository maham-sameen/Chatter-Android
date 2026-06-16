package com.example.chatter.features.contacts.data.repository

import androidx.room.withTransaction
import com.example.chatter.AppDatabase
import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.LocalImageStorage
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.remote.GetDownloadUrl
import com.example.chatter.core.data.data_source.remote.UsersDataStore
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.User
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import com.example.chatter.features.contacts.data.data_source.mapper.ChatRoomResponseToChatRoom
import com.example.chatter.features.contacts.data.data_source.mapper.ChatRoomToChatRoomRequest
import com.example.chatter.features.contacts.data.data_source.mapper.ChatRoomToDbs
import com.example.chatter.features.contacts.data.data_source.mapper.ContactEntityToContact
import com.example.chatter.features.contacts.data.data_source.mapper.ContactToContactEntity
import com.example.chatter.core.data.data_source.model.remote.ChatRoomResponse
import com.example.chatter.core.data.data_source.remote.UserService
import com.example.chatter.features.contacts.data.data_source.remote.ChatRoomService
import com.example.chatter.features.contacts.domain.model.ChatRoom
import com.example.chatter.features.contacts.domain.model.Contact
import com.example.chatter.features.contacts.domain.repository.ContactRepo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.serialization.json.Json

class ContactRepoImp(
    private val contactDao: ContactDao ,
    private val contactToContactEntity: ContactToContactEntity ,
    private val contactEntityToContact: ContactEntityToContact ,
    private val userDataSource: UserDataSource ,
    private val client: HttpClient ,
    private val localImageStorage: LocalImageStorage ,
    private val getDownloadUrl: GetDownloadUrl ,
    private val chatRoomToDbs: ChatRoomToDbs ,
    private val memberDao: MemberDao ,
    private val chatRoomDao: ChatRoomDao ,
    private val usersDataStore: UsersDataStore ,
    private val chatRoomToChatRoomRequest: ChatRoomToChatRoomRequest ,
    private val chatRoomResponseToChatRoom: ChatRoomResponseToChatRoom ,
    private val appDatabase: AppDatabase ,
    private val userService: UserService
) : ContactRepo {

    override suspend fun insertContact(contact: Contact): Boolean {
        return contactDao.upsert(
            contactToContactEntity.map(contact)
        )!= -1L
    }

    override suspend fun getCurrentUserId(): String {
        return userDataSource.user.firstOrNull()?.phoneNumber?.toString() ?:""
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override  fun getAllContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts()
            .mapLatest {
                it.map{ contactEntityToContact.map(it) }
            }
    }

    override suspend fun getFullContact(contact: Contact): Result<Contact> {
        println("\uD83D\uDE1B [ContactRepoImp] getFullContact")
        return userService.getContactInfo(
            phoneNumber = PhoneNumber(
                countryCode = contact.phoneNumber.countryCode,
                phoneNumber = contact.phoneNumber.phoneNumber
            )
        ).fold(
            onFailure = {
                println("😛[ContactRepoImp] getFullContact , onFailure : ${it.toString()} ")
                Result.failure(it) } ,
            onSuccess = {contactInfo->
                println("😛[ContactRepoImp] getFullContact , contactInfo $contactInfo ")
                Result.success(Contact(
                    userId = contact.userId,
                    name = contact.name,
                    phoneNumber = contact.phoneNumber,
                    image = contactInfo.image,
                    colorScheme = ColorScheme(
                        background = contactInfo.colorScheme.background,
                        icon = contactInfo.colorScheme.icon
                    ),
                    isUser = contact.isUser
                ))
            }
        )
    }

    override suspend fun getContactViaPhoneNumber(
        countryCode: String,
        phoneNumber: String
    ): Contact? {
        return contactDao.getContactByPhoneNumber(
            countryCode = countryCode,
            phoneNumber = phoneNumber
        )?.let {
            println("\uD83D\uDE1B [ContactRepoImp] getContactViaPhoneNumber contactEntity : $it")
            contactEntityToContact.map(it)
        }
    }

    override suspend fun uploadChatRoomImage(
        image: ByteArray,
    ): Result<String> {
        println("\uD83D\uDE1B [ContactRepoImp] uploadChatRoomImage , image ${image.toString()}")
        return runCatching {
           val filePath = localImageStorage.writeData(
               data = image,
           ) ?: throw Exception("Can not save image internally")
            println("\uD83D\uDE1B [ContactRepoImp] uploadChatRoomImage , filePath : ${filePath.toString()}")
            val imageByte = localImageStorage.readBytesFromPath(filePath) ?: throw Exception("can not read bytes")
            println("\uD83D\uDE1B [ContactRepoImp] uploadChatRoomImage , imageBytes get")
            getDownloadUrl.uploadImage(imageByte) ?:throw Exception("can not upload image")
       }
    }

    override suspend fun insertChatRoom(
        chatRoom: ChatRoom ,
        phoneNumber: com.example.chatter.core.domain.model.PhoneNumber
    ): Result<Unit> {
        return appDatabase.withTransaction{
            println("\uD83D\uDE1B [ContactRepoImp] insertChatRoom , chatRoom : ${chatRoom.toString()} , phoneNumber : ${phoneNumber.toString()}")
            val dbs = chatRoomToDbs.map(chatRoom)
            println("\uD83D\uDE1B [ContactRepoImp] insertChatRoom , dbs : ${dbs.toString()}")
            chatRoomDao.deleteChatRoomByRoomId(chatRoom.roomId)
            val chatRoomResult = chatRoomDao.upsert(dbs.first) != -1L
            val membersResults = dbs.second.map {
                println("\uD83D\uDE1B [ContactRepoImp] insertChatRoom , insertMember : ${it.toString()}")
                if (it.phoneNumber.countryCode != phoneNumber.countryCode || it.phoneNumber.phoneNumber != phoneNumber.phoneNumber) {
                    println("\uD83D\uDE1B [ContactRepoImp] insertChatRoom , if condition true")
                    memberDao.deleteMember(
                        id = it.roomId,
                        countryCode = it.phoneNumber.countryCode,
                        phoneNumber = it.phoneNumber.phoneNumber
                    )
                    memberDao.upsert(it) != -1L
                } else {
                    println("\uD83D\uDE1B [ContactRepoImp] insertChatRoom , else is true")
                    true
                }
            }
            println("\uD83D\uDE1B [ContactRepoImp] insertChatRoom , insertMemberEntities result : $membersResults , chatRoomResult : $chatRoomResult")
            if( membersResults.any { !it} || !chatRoomResult){ throw Exception("Internal Error") }
             Result.success(Unit)
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        println("\uD83D\uDE1B [ContactRepoImp]  getAllUsers()")
        return usersDataStore.getAllUsers()
    }

    override suspend fun saveChatRoom(chatRoom: ChatRoom): Result<ChatRoom> {
        println("\uD83D\uDE1B [ContactRepoImp] saveChatRoom , chatRoom  ${chatRoom.toString()}")
        return runCatching {
            val response = client.post {
                url(ChatRoomService.EndPoints.SaveChatRoom.url)
                setBody(chatRoomToChatRoomRequest.map(chatRoom))
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B [ContactRepoImp] saveChatRoom body $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val chatRoomResponse = Json.decodeFromString<ChatRoomResponse>(body)
                    println("\uD83D\uDE1B [ContactRepoImp] saveChatRoom , chatRoomResponse $chatRoomResponse")
                    chatRoomResponseToChatRoom.map(chatRoomResponse)
                }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }

    override suspend fun updateLastSeenByPhoneNumber(
        lastSeen: Long ,
        phoneNumber: String,
        countryCode: String
    ) {
        println("\uD83D\uDE1B [ContactRepoImp] updateLastSeenAndImageByPhoneNumber , lastSeen : $lastSeen  , phoneNumber : $phoneNumber , countryCode : $countryCode")
        contactDao.updateLastSeenByPhoneNumber(
            countryCode = countryCode,
            number = phoneNumber,
            lastSeen = lastSeen,
        )
    }
}