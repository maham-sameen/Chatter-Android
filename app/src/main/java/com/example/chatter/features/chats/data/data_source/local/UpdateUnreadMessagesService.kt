package com.example.chatter.features.chats.data.data_source.local

import androidx.room.withTransaction
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.chatter.AppDatabase
import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.local.ChatRoomEntity
import com.example.chatter.core.data.data_source.model.local.MemberEntity
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.remote.ConversationRoom
import com.example.chatter.features.chats.data.data_source.model.remote.GetConversationRoomRequest
import com.example.chatter.features.chats.data.data_source.remote.ChatRoomService
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class UpdateUnreadMessagesService(
    private val chatRoomDao : ChatRoomDao ,
    private val client : HttpClient ,
    private val userDataSource: UserDataSource ,
    private val memberDao: MemberDao ,
    private val contactDao: ContactDao ,
    private val appDatabase: AppDatabase
) {

    suspend fun updateUnreadMessages(
        roomId : String ,
        timeStamp : Long ,
        lastMessage : String ,
    ){
        println("🌱 [UpdateUnreadMessageService] START updateUnreadMessages")
        println("🌱 UpdateUnreadMessageService] roomId = $roomId")
        println("🌱 UpdateUnreadMessageService] timeStamp = $timeStamp")
        println("🌱 UpdateUnreadMessageService] lastMessage = $lastMessage")

        try {
            println("🔍 UpdateUnreadMessageService] Fetching chatRoom from DB...")
            val chatRoom =
                chatRoomDao.getChatRoomById(roomId).firstOrNull()?.firstOrNull()

            println("🔍 UpdateUnreadMessageService] chatRoom result = $chatRoom")

            if (chatRoom != null) {
                println("✏️ UpdateUnreadMessageService] Updating existing chatRoom...")
                println("✏️ UpdateUnreadMessageService] Old unread = ${chatRoom.unreadMessages}")
                println("✏️ UpdateUnreadMessageService] New unread = ${chatRoom.unreadMessages + 1}")

                chatRoomDao.updateChatRoom(
                    roomId = roomId,
                    lastMessage = lastMessage,
                    timeStamp = timeStamp,
                    unreadMessages = chatRoom.unreadMessages + 1
                )

                println("✅ UpdateUnreadMessageService] ChatRoom updated successfully!")
            } else {
                println("⚠️ UpdateUnreadMessageService] chatRoom NOT FOUND → Fetching from server (and applying FCM payload)...")
                // Pass the FCM payload into the fetcher so the row is created
                // with the right lastMessage / timeStamp / unread in a single transaction.
                fetchAndSaveChatRoom(
                    roomId = roomId,
                    lastMessage = lastMessage,
                    timeStamp = timeStamp,
                )
            }

        } catch (e: Exception) {
            println("❌ [UpdateUnreadMessageService] Exception in updateUnreadMessages: ${e.message}")
            e.printStackTrace()
        }

        println("🌱 [UpdateUnreadMessageService] END updateUnreadMessages")
    }


    suspend fun fetchAndSaveChatRoom(
        roomId : String,
        lastMessage: String? = null,
        timeStamp: Long? = null,
    ){
        println("😛 [UpdateUnreadMessageService] START fetchAndSaveChatRoom")
        println("😛 UpdateUnreadMessageService] roomId = $roomId")
        println("😛 UpdateUnreadMessageService] FCM lastMessage = $lastMessage")
        println("😛 UpdateUnreadMessageService] FCM timeStamp   = $timeStamp")

        userDataSource.user.firstOrNull()?.phoneNumber?.let { phoneNumber ->
            println("👤 UpdateUnreadMessageService] Current user phone = $phoneNumber")

            fetchConversationRoom(
                roomId = roomId,
                phoneNumber = phoneNumber
            ).fold(
                onSuccess = { conversationRoom ->
                    println("✅ UpdateUnreadMessageService] fetchConversationRoom SUCCESS")
                    println("✅ UpdateUnreadMessageService] conversationRoom = $conversationRoom")

                    // FCM payload wins: this row should reflect THIS incoming message,
                    // not whatever the server has cached for the room.
                    val resolvedLastMessage = lastMessage ?: conversationRoom.lastMessage
                    val resolvedTimeStamp = timeStamp ?: conversationRoom.timeStamp
                    val resolvedUnread = if (lastMessage != null) 1 else conversationRoom.unreadMessageCount

                    appDatabase.withTransaction{
                        println("💾 UpdateUnreadMessageService] Saving chatRoom in DB...")

                        chatRoomDao.upsert(
                            ChatRoomEntity(
                                roomId = roomId,
                                timeStamp = resolvedTimeStamp,
                                image = conversationRoom.image,
                                name = conversationRoom.name,
                                colorScheme = conversationRoom.colorScheme,
                                lastMessage = resolvedLastMessage,
                                unreadMessages = resolvedUnread
                            )
                        )

                        println("💾 UpdateUnreadMessageService] ChatRoom saved in DB")

                        conversationRoom.members.forEach {
                            println("👥 UpdateUnreadMessageService] Processing member: ${it.phoneNumber}")

                            if (it.phoneNumber != phoneNumber) {
                                println("➕ UpdateUnreadMessageService] Inserting member & contact")
                                memberDao.deleteMember(
                                    id = roomId,
                                    countryCode = it.phoneNumber.countryCode,
                                    phoneNumber = it.phoneNumber.phoneNumber
                                )
                                contactDao.deleteContact(
                                    userId = phoneNumber.toString(),
                                    countryCode = it.phoneNumber.countryCode,
                                    phoneNumber = it.phoneNumber.phoneNumber
                                )
                                contactDao.upsert(
                                    ContactEntity(
                                        userId = phoneNumber.toString(),
                                        name = it.name,
                                        phoneNumber = it.phoneNumber,
                                        isUser = it.phoneNumber == phoneNumber,
                                        image = it.image,
                                        colorScheme = it.colorScheme,
                                        lastSeen = it.lastSeen
                                    )
                                )

                                memberDao.upsert(
                                    MemberEntity(
                                        roomId = roomId,
                                        phoneNumber = it.phoneNumber
                                    )
                                )
                            } else {
                                println("⏭️ UpdateUnreadMessageService] Skipping self member")
                            }
                        }
                    }
                },
                onFailure = {
                    println("❌ UpdateUnreadMessageService] fetchConversationRoom FAILED: ${it.message}")
                    return@fold
                }
            )
        } ?: run {
            println("⚠️ UpdateUnreadMessageService] User is NULL in userDataSource")
        }

        println("😛 [UpdateUnreadMessageService] END fetchAndSaveChatRoom")
    }


    suspend fun fetchConversationRoom(
        roomId : String ,
        phoneNumber: PhoneNumber
    ) : Result<ConversationRoom>{

        println("🌐 [UpdateUnreadMessageService] START fetchConversationRoom")
        println("🌐 [UpdateUnreadMessageService] roomId = $roomId")
        println("🌐 [UpdateUnreadMessageService] phoneNumber = $phoneNumber")

        return runCatching {
            val response = client.post {
                url(ChatRoomService.EndPoints.GetConversationRooms.url)
                setBody(
                    GetConversationRoomRequest(
                        roomId = roomId,
                        phoneNumber = phoneNumber
                    )
                )
                contentType(ContentType.Application.Json)
            }

            val body = response.body<String>()
            println("📡 [UpdateUnreadMessageService] Server Response Status = ${response.status}")
            println("📡 [UpdateUnreadMessageService] Server Response Body = $body")

            when (response.status) {
                HttpStatusCode.OK -> {
                    println("✅ [UpdateUnreadMessageService] ConversationRoom decoded successfully")
                    Json.decodeFromString(body)
                }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }


    fun uploadDateToWorker(
        roomId : String ,
        timeStamp : Long ,
        lastMessage : String ,
        workManager: WorkManager
    ) {
        println("🚀 [UpdateUnreadMessageService] START uploadDateToWorker")
        println("🚀 [UpdateUnreadMessageService] roomId = $roomId")
        println("🚀 [UpdateUnreadMessageService] timeStamp = $timeStamp")
        println("🚀 [UpdateUnreadMessageService] lastMessage = $lastMessage")

        val timestampGson = Gson().toJson(timeStamp)
        println("🧾 [UpdateUnreadMessageService] timestampGson = $timestampGson")

        val data = workDataOf(
            Pair("time_stamp", timestampGson),
            Pair("room_id", roomId),
            Pair("last_message", lastMessage)
        )

        val request = OneTimeWorkRequestBuilder<UpdateUnreadMessagesWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        println("📤 [UpdateUnreadMessageService] Enqueuing WorkManager request...")
        workManager.enqueue(request)

        println("✅ [UpdateUnreadMessageService] WorkManager request enqueued successfully")
        println("🚀 [UpdateUnreadMessageService] END uploadDateToWorker")
    }
}
