package com.example.chatter.features.chats.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import androidx.work.WorkManager
import com.example.chatter.AppDatabase
import com.example.chatter.core.data.data_source.local.ChatRoomDao
import com.example.chatter.core.data.data_source.local.MemberDao
import com.example.chatter.core.data.data_source.local.MessageDao
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.core.data.data_source.remote.UserService
import com.example.chatter.core.domain.model.ColorScheme
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.data.data_source.local.MessageRemoteKeyDao
import com.example.chatter.features.chats.data.data_source.local.UpdateUnreadMessagesService
import com.example.chatter.features.chats.data.data_source.mapper.ContactEntityToUser
import com.example.chatter.features.chats.data.data_source.mapper.MessageDomainToMessageEntity
import com.example.chatter.features.chats.data.data_source.mapper.MessageEntityToMessage
import com.example.chatter.features.chats.data.data_source.mapper.MessageToMessageEntity
import com.example.chatter.features.chats.data.data_source.model.remote.GetUpdatedChatRoomRequest
import com.example.chatter.features.chats.data.data_source.model.remote.GetUpdatedChatRoomResponse
import com.example.chatter.features.chats.data.data_source.model.remote.ClosedChatMsg
import com.example.chatter.features.chats.data.data_source.pagingSource.MessageRemoteMediator
import com.example.chatter.features.chats.data.data_source.remote.ChatRoomService
import com.example.chatter.features.chats.data.data_source.remote.ChatSocketService
import com.example.chatter.features.chats.data.data_source.remote.MessageService
import com.example.chatter.features.chats.data.data_source.remote.PresenceSocketService
import com.example.chatter.features.chats.domain.model.ChatRoom
import com.example.chatter.features.chats.domain.model.ConversationRoom
import com.example.chatter.features.chats.domain.model.Message
import com.example.chatter.features.chats.domain.model.User
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.example.chatter.features.chats.domain.repository.OpenedChatMsg
import com.example.chatter.features.contacts.data.data_source.local.ContactDao
import com.example.chatter.features.contacts.data.data_source.model.local.ContactEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.collections.flatten
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class ChatRepoImp(
    private val userDataSource: UserDataSource,
    private val client: HttpClient,
    private val presenceSocketService: PresenceSocketService,
    private val chatRoomDao: ChatRoomDao,
    private val contactDao: ContactDao,
    private val memberDao: MemberDao,
    private val contactEntityToUser: ContactEntityToUser,
    private val chatSocketService: ChatSocketService,
    private val messageDao: MessageDao,
    private val messageRemoteKeyDao: MessageRemoteKeyDao,
    private val messageToMessageEntity: MessageToMessageEntity,
    private val messageService: MessageService,
    private val messageEntityToMessage: MessageEntityToMessage,
    private val updateUnreadMessagesService: UpdateUnreadMessagesService ,
    private val userService: UserService ,
    private val appDatabase: AppDatabase ,
) : ChatRepo {


    var job : Job? = null


    @OptIn(ExperimentalCoroutinesApi::class)

    /**
     * if the name of the chat is empty it means that is one to one to chat means
     * that have two member and one is user and other is member
     * as we are using the app so  in database there is only one member
     */
    override  fun getAllChatRooms(): Flow<List<ChatRoom>> {
        println("\uD83D\uDC05 [ChatRepoImp] getAllChatsRooms() ")
        return chatRoomDao
            .getAllChats()
            .mapLatest {chats ->
                println("\uD83D\uDC05 [ChatRepoImp] getAllChatsRooms() , chats : ${chats.toString()} ")
                chats.map { chat ->
                    val contact : ContactEntity? = if(chat.name.isEmpty() && chat.image == null){
                        println("\uD83D\uDC05 [ChatRepoImp] getAllChatsRooms()  chat name is empty and image is null ")
                        val members = memberDao.getRoomMembers(chat.roomId)
                        println("\uD83D\uDC05 [ChatRepoImp] getAllChatsRooms() members : ${members.toString()}")
                        if(members.size == 1){
                            contactDao.getContactByPhoneNumber(
                                countryCode = members[0].phoneNumber.countryCode,
                                phoneNumber = members[0].phoneNumber.phoneNumber
                            )
                        }else{
                            null
                        }
                    } else {
                        null
                    }
                    val color = contact?.colorScheme ?:chat.colorScheme
                    println("\uD83D\uDC05 [ChatRepoImp] getAllChatsRooms() contact : ${contact.toString()} ")
                    ChatRoom(
                        roomId = chat.roomId,
                        name = contact?.name  ?: chat.name,
                        image = contact?.image ?: chat.image,
                        colorScheme = color?.let {
                            ColorScheme(
                                background = it.background,
                                icon = it.icon
                            )
                        } ,
                        timeStamp = chat.timeStamp,
                        lastMessage = chat.lastMessage ,
                        unreadMessages = chat.unreadMessages
                    )
                }
            }
    }

    override suspend fun isTokenValid(): Boolean {
        println("\uD83D\uDC05 [ChatRepoImp] isTokenValid ")
        return userDataSource.data.firstOrNull()?.let {
            println("\uD83D\uDC05 [ChatRepoImp] isTokenValid userPreference : $it")
            val response  = client.get {
                url(UserService.EndPoints.Authenticate.url)
                header("Authorization", "Bearer ${it.userToken}")
            }
            println("\uD83D\uDC05 [ChatRepoImp] isTokenValid response : $response")
            response.status == HttpStatusCode.OK
        } ?:false
    }


    /**
     * a) combine(chatRoomDao.getChatRoomById(roomId), memberDao.getRoomMembersOfRoom(roomId))
     *
     * This combines two flows:
     *
     * Flow of chat room (Flow<List<ChatRoomEntity>>)
     *
     * Flow of members (Flow<List<MemberEntity>>)
     *
     * Every time either flow emits a new value, combine emits a pair (chatRoom, members).
     *
     * b) .filter { (chatRoom, members) -> ... }
     *
     * We ignore empty rooms or empty member lists, because we need both to build a conversation.
     *
     * c) .flatMapLatest { (chatRoom, members) -> ... }
     *
     * flatMapLatest is used because we want:
     *
     * If the chatRoom or members change, all downstream flows cancel and a new flow starts.
     *
     * This is perfect because contactDao.getContact returns a flow, and we want it to update automatically.
     *
     * d) contactFlows = members.map { ... }
     *
     * For each member, we get their contact as a Flow<List<ContactEntity>>.
     *
     * So we now have a list of flows, one per member.
     *
     * e) combine(contactFlows) { contactLists -> ... }
     *
     * combine merges all member contact flows into a single emission every time any member's contact changes.
     *
     * contactLists is an array of List<ContactEntity> (one for each member).
     *
     * f) .toList().flatten()
     *
     * contactLists.toList() converts the array into a Kotlin List<List<ContactEntity>>.
     *
     * .flatten() merges all inner lists into one flat list of contacts, so we can use it directly in ConversationRoom.
     *
     * Without flatten(), we would get List<List<ContactEntity>> which is nested and harder to work with.
     *
     * g) .map { contacts -> ConversationRoom(...) }
     *
     * Now, each time chat room, members, or any contact changes, we emit a new ConversationRoom.
     *
     * This ensures the UI always has the latest combined state.
     */

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getConversationRoom(roomId: String): Flow<ConversationRoom> {
        println("\uD83D\uDC05 [ChatRepoImp] getConversationRoom() , roomId : $roomId")
        return combine(
            chatRoomDao.getChatRoomById(roomId),
            memberDao.getRoomMembersOfRoom(roomId)
        ) { chatRooms, members ->
            println("\uD83D\uDC05 [ChatRepoImp] getConversationRoom()  Combine emitted — chatRooms: $chatRooms, members: $members")
            chatRooms.firstOrNull() to members
        }
            // Only require the chat row to exist. We intentionally do NOT filter on
            // members.isNotEmpty() — a group room whose `members` table is empty for
            // any reason would otherwise cause the downstream `firstOrNull()` in
            // GetChatState to suspend forever, leaving the conversation screen on
            // an infinite loading spinner. Letting the empty-members case through
            // gives GetChatState a chance to return null → VM shows a real error.
            .filter { (chatRoom, _) -> chatRoom != null }
            .flatMapLatest { (chatRoom, members) ->
                println("\uD83D\uDC05 [ChatRepoImp] getConversationRoom()  FlatMapLatest  triggered — chatRoom: $chatRoom, members: $members")

                val contactFlows = members.map { member ->
                    println("\uD83D\uDC05 [ChatRepoImp] getConversationRoom()  Creating contact flow for member: ${member.phoneNumber}")
                    appDatabase.withTransaction{
                        userService.getContactInfo(
                            phoneNumber = com.example.chatter.core.data.data_source.model.local.PhoneNumber(
                                countryCode = member.phoneNumber.countryCode,
                                phoneNumber = member.phoneNumber.phoneNumber
                            )
                        ).fold(
                            onSuccess = {
                                println("[ChatRepoImp] getConversationRoom()  , contactInfo : ${it.toString()}")
                                contactDao.updateLastSeenAndImageByPhoneNumber(
                                    countryCode = it.phoneNumber.countryCode,
                                    number = it.phoneNumber.phoneNumber,
                                    lastSeen = it.lastSeen,
                                    image = it.image
                                )
                            },
                            onFailure = {
                                println("[ChatRepoImp] getConversationRoom()  , onFailure : ${it.toString()}")
                            }
                        )
                        contactDao.getContact(
                            countryCode = member.phoneNumber.countryCode,
                            phoneNumber = member.phoneNumber.phoneNumber
                        )
                    }
                }

                val combinedContactsFlow = if (contactFlows.isEmpty()) {
                    println("\uD83D\uDC05 No members, emitting empty contacts list")
                    flowOf(emptyList<ContactEntity>())
                } else {
                    combine(contactFlows) { contactLists ->
                        println("\uD83D\uDC05 Combine contactFlows emitted — contactLists: ${contactLists.contentToString()}")
                        contactLists.toList().flatten<ContactEntity>()
                    }
                }

                combinedContactsFlow.map { contacts ->
                    println("\uD83D\uDC05 Rebuilding ConversationRoom — contacts: $contacts")

                    ConversationRoom(
                        roomId = roomId,
                        image = chatRoom!!.image,
                        name = chatRoom.name,
                        timeStamp = chatRoom.timeStamp,
                        colorScheme = chatRoom.colorScheme?.let { (background, icon) ->
                            ColorScheme(background, icon)
                        },
                        members = contacts.map { contactEntityToUser.map(it) }
                    )
                }
            }
    }

    override suspend fun sendMemberInfo(memberInfo: Any) {
        presenceSocketService.sendMemberInfo(memberInfo = memberInfo)
    }

    override fun observerMemberInfo(): SharedFlow<Any?> {
       return presenceSocketService.observerMemberInfo()
    }

    override suspend fun getUserPhoneNumber(): String {
        return userDataSource.user.firstOrNull()?.phoneNumber?.toString() ?: ""
    }

    override suspend fun updateContactLastSeen(
        lastSeen: Long,
        phoneNumber: String,
        countryCode: String
    ): Int {
        return contactDao
            .updateLastSeenByPhoneNumber(
                countryCode = countryCode,
                number = phoneNumber,
                lastSeen = lastSeen
            )
    }

    override suspend fun initSession(membersPhoneNumber : List<String> , scope : CoroutineScope): Result<Unit> {
         val phoneNumber = getUserPhoneNumber()
         return  if(phoneNumber.isNotEmpty()){
             chatSocketService.initSession(
                 UserSession(
                     userPhone = phoneNumber,
                     contactPhone = membersPhoneNumber
                 ),
                 scope = scope
             )
         }else{
             Result.failure(Exception("User not exist"))
         }
    }

    override suspend fun closeSession() {
        chatSocketService.closeSession()
    }

    override suspend fun sendMessage(message: String) {
        chatSocketService.sendMessage(json = message)
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    override fun getMessageRemoteMediator(roomId: String , phoneNumbers: List<PhoneNumber>): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 1,
                enablePlaceholders = true,
                initialLoadSize = 10
            ) ,
            pagingSourceFactory = {
                 messageDao.getMessages(roomId)
            } ,
            remoteMediator = MessageRemoteMediator(
                messageRemoteKeyDao = messageRemoteKeyDao,
                messageDao = messageDao,
                messageService = messageService,
                roomId = roomId,
                messageToMessageEntity = messageToMessageEntity,
                phoneNumbers = phoneNumbers.map{
                    com.example.chatter.core.data.data_source.model.local.PhoneNumber(
                        countryCode = it.countryCode,
                        phoneNumber = it.phoneNumber
                    )
                },
            )
        ).flow.mapLatest { pagingData ->
            println("\uD83E\uDD3C\u200D♂\uFE0F  [ChatRepoImp] , pagingData : ${pagingData.toString()}")
            pagingData.map{
               messageEntity ->
                println("\uD83E\uDD3C\u200D♂\uFE0F  [ChatRepoImp] , messageEntity : $messageEntity")
                messageEntityToMessage.map(messageEntity)
            }
        }
    }

    override fun observeMessage(): SharedFlow<Any?> {
        return chatSocketService.observerMessage()
    }

    override suspend fun saveMessage(message: Message , roomId: String) {
        val messageDomainToMessageEntity = MessageDomainToMessageEntity(roomId = roomId)
        messageDao.upsert(messageDomainToMessageEntity.map(message))
    }

    override suspend fun sendClosedChatMsg(closedChatMsg: com.example.chatter.features.chats.domain.model.ClosedChatMsg) {
        println("\uD83E\uDD47 [ChatRepoImp] , SendOpenChatMessage , ${closedChatMsg.toString()}")
        userDataSource.user.firstOrNull()?.let{
            val closedChatMsgDb =ClosedChatMsg(
                roomId = closedChatMsg.roomId,
                userPhoneNumber = it.phoneNumber,
                timeStamp = closedChatMsg.timeStamp
            )
            println("\uD83E\uDD47 [ChatRepoImp] , SendOpenChatMsgDb , ${closedChatMsgDb.toString()}")
            job = CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                updateChatRoom(closedChatMsg.roomId)
                    .fold(
                        onSuccess = {
                            job?.cancel()
                                    job = null},
                        onFailure = {
                            job?.cancel()
                            job = null
                        }
                    )
            }
            val json = Json.encodeToString(closedChatMsgDb)
            chatSocketService.sendMessage(json = json)
        }
    }

    override suspend fun markMessagesAsRead(
        roomId: String
    ) {
        println("\uD83D\uDDFD [ChatRepoImp] , roomId : $roomId")
            chatRoomDao.updateChatRoomUnreadMessages(
                roomId = roomId,
                unreadMessages = 0
            )
            messageDao.markMessagesAsRead(
                roomId = roomId,
                isRead = true
            )
    }

    override suspend fun updateChatRoom(roomId: String): Result<Unit> {
        println("\uD83E\uDD3C\u200D♂\uFE0F  [ChatRepoImp] , updatedChatRoom , roomId : $roomId")
        return userDataSource.user.firstOrNull()?.let {
            val getUpdatedChatRoomRequest = GetUpdatedChatRoomRequest(
                roomId = roomId,
                phoneNumber = it.phoneNumber
            )
            runCatching {
                val response = client.post {
                    url(ChatRoomService.EndPoints.GetUpdatedChatRoom.url)
                    setBody(getUpdatedChatRoomRequest)
                    contentType(ContentType.Application.Json)
                }
                val body = response.body<String>()
                println("\uD83D\uDE1B [ChatRepoImp] , updatedChatRoom body $body")
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val getUpdatedChatRoomResponse = Json.decodeFromString<GetUpdatedChatRoomResponse>(body)
                        println("\uD83D\uDE1B [ChatRepoImp] , updatedChatRoom  getUpdatedChatRoomResponse : $getUpdatedChatRoomResponse")
                        chatRoomDao.updateChatRoom(
                            roomId = roomId,
                            lastMessage = getUpdatedChatRoomResponse.lastMessage,
                            timeStamp = getUpdatedChatRoomResponse.timeStamp,
                            unreadMessages = getUpdatedChatRoomResponse.unreadMessages
                        )
                    }
                    HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                    else -> throw Exception("Unexpected error: $body")
                }
            }
        } ?: Result.failure(Exception("User do not exist"))
    }

    override fun uploadDataToWorker(
        roomId: String,
        timeStamp: Long,
        lastMessage: String,
        workManager: WorkManager
    ) {
        updateUnreadMessagesService.uploadDateToWorker(
            roomId = roomId,
            timeStamp = timeStamp,
            lastMessage = lastMessage,
            workManager = workManager
        )
    }

    override suspend fun updateUnreadMessages(
        roomId: String,
        timeStamp: Long,
        lastMessage: String
    ) {
        updateUnreadMessagesService.updateUnreadMessages(
            roomId = roomId,
            timeStamp = timeStamp,
            lastMessage = lastMessage
        )
    }

    override suspend fun sendOpenedChatMsg(openChatMsg: OpenedChatMsg) {
        println("\uD83C\uDF41 [ChatRepoImp] sendOpenedChatMsg : ${openChatMsg.toString()}")
        userDataSource.user.firstOrNull()?.let {
            println("\uD83C\uDF41 [ChatRepoImp] phoneNumber : ${it.phoneNumber}")
            val json = Json.encodeToString(
                com.example.chatter.features.chats.data.data_source.model.remote.OpenedChatMsg(
                    roomId = openChatMsg.roomId,
                    userPhoneNumber = it.phoneNumber
                )
            )
            chatSocketService.sendMessage(json)
        }
    }

    override fun clearLastChat() {
        return chatSocketService.clearLastChat()
    }
}