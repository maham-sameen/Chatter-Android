package com.example.chatter.features.chats.presentation.conversation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chatter.core.presentation.model.UIState
import com.example.chatter.features.chats.domain.model.ChatOpened
import com.example.chatter.features.chats.domain.model.Message
import com.example.chatter.features.chats.domain.model.Presence
import com.example.chatter.features.chats.domain.model.Typing
import com.example.chatter.features.chats.domain.use_case.conversation.ConversationUseCases
import com.example.chatter.features.chats.presentation.conversation.model.ChatState
import com.example.chatter.features.chats.presentation.conversation.model.dummyChatState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import com.example.chatter.features.chats.presentation.conversation.model.Message as UiMessage

class ConversationViewModel(
    private val conversationUseCases: ConversationUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Idle)
    val uiState: StateFlow<UIState> = _uiState

    private val _chatState = MutableStateFlow(dummyChatState.copy(
        name = "",
        image = null,
        lastSeen = "",
        isOnline = false,
        isTyping = false,
        members = emptyList(),
        membersNames = "",
        roomId = "",
        presenceText = ""
    ))
    val chatState: StateFlow<ChatState> = _chatState

    private val _message = mutableStateOf("")
    val message: State<String> = _message

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _messagesSource = MutableStateFlow<Flow<PagingData<UiMessage>>>(emptyFlow())

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: Flow<PagingData<UiMessage>> = _messagesSource
        .flatMapLatest { it }
        .cachedIn(viewModelScope)

    val callees = mutableStateListOf<String>()

    private var currentRoomId: String? = null
    private val anyMemberOffline = MutableStateFlow(false)
    private var observeMemberInfosJob: Job? = null
    private var observeMessageJob: Job? = null

    /**
     * Tracks which roomId the chat socket is currently initialized for.
     * - `GetChatState` use case already opens the socket on first load → we record the roomId.
     * - `handleChatClose` clears it on background → next `ChatOpen` re-opens.
     * - `handleChatOpen` only calls `initChatSession` if this flag != currentRoomId,
     *   so we never double-init and race with the use case.
     */
    private var chatSocketInitializedFor: String? = null

    /**
     * Periodic presence-sync loop. Re-sends `sendMemberInfo(sendPresence=true)` every
     * `PRESENCE_POLL_MILLIS` while the conversation is open. The server's `sendPresence`
     * handler replies with the live online status of every other member — so any offline
     * transition is reflected in our top bar within one poll interval, instead of relying
     * on the server's broadcast latency (which we observed at ~27s).
     */
    private var presencePollingJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: ConversationEvent) {
        when (event) {
            is ConversationEvent.GetChatState -> handleGetChatState(event.roomId)
            is ConversationEvent.EnterMessage -> handleEnterMessage(event.text)
            ConversationEvent.SendMessage -> handleSendMessage()
            ConversationEvent.ChatOpen -> handleChatOpen()
            ConversationEvent.ChatClose -> handleChatClose()
            ConversationEvent.ClearUIState -> _uiState.value = UIState.Idle
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleGetChatState(roomId: String) {
        println("⭐ [VM/handleGetChatState] roomId=$roomId")
        currentRoomId = roomId
        viewModelScope.launch {
            _uiState.value = UIState.Loading

            println("⏱ [VM/handleGetChatState] enter getChatState")
            val state = withTimeoutOrNull(LOAD_STEP_TIMEOUT_MILLIS) {
                conversationUseCases.getChatState(roomId, viewModelScope)
            }
            println("⏱ [VM/handleGetChatState] exit  getChatState -> ${state != null}")
            if (state == null) {
                println("⭐ [VM/handleGetChatState] -> state==null, setting UIState.Error")
                _uiState.value = UIState.Error("Chat not found")
                return@launch
            }
            println("⭐ [VM/handleGetChatState] -> state loaded: members=${state.members.size}, names='${state.membersNames}', isOnline=${state.isOnline}, presenceText='${state.presenceText}'")
            _chatState.value = state
            anyMemberOffline.value = state.members.any { !it.isOnline }
            _messagesSource.value = conversationUseCases.getMessages(roomId, state.members)
            callees.clear()
            println("⏱ [VM/handleGetChatState] enter getCallees")
            val callees0 = withTimeoutOrNull(LOAD_STEP_TIMEOUT_MILLIS) {
                conversationUseCases.getCallees(state.members.map { it.phoneNumber.toString() })
            }
            println("⏱ [VM/handleGetChatState] exit  getCallees -> size=${callees0?.size}")
            callees0?.let { callees.addAll(it) }
            println("⏱ [VM/handleGetChatState] enter markMessagesAsRead")
            withTimeoutOrNull(LOAD_STEP_TIMEOUT_MILLIS) {
                conversationUseCases.markMessagesAsRead(roomId)
            }
            println("⏱ [VM/handleGetChatState] exit  markMessagesAsRead")
            // GetChatState use case already opened the chat socket on success.
            // Record it so `handleChatOpen` (fired by ON_RESUME) doesn't race-reopen it.
            chatSocketInitializedFor = roomId
            startObservingMemberInfos()
            startObservingMessages(roomId)
            _uiState.value = UIState.Idle
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    //   Presence / Typing — derived in the ViewModel against the LIVE chat state
    // ────────────────────────────────────────────────────────────────────────────

    private fun startObservingMemberInfos() {
        println("🔵 [VM] startObservingMemberInfos() — (re)subscribing to presence socket events")
        observeMemberInfosJob?.cancel()
        observeMemberInfosJob = viewModelScope.launch {
            conversationUseCases.observeMemberInfos().collect { frame ->
                println("🔵 [VM/observeMemberInfos.collect] received frame=$frame (type=${frame?.let { it::class.simpleName }})")
                when (frame) {
                    is Presence -> handlePresence(frame)
                    is Typing -> handleTyping(frame)
                    else -> println("🔵 [VM/observeMemberInfos.collect] -> IGNORED unknown frame type")
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun handlePresence(frame: Presence) {
        val snapshot = _chatState.value
        val isOneToOneForThisMember =
            !snapshot.membersNames.contains(",") &&
                    snapshot.members.size == 1 &&
                    snapshot.members.first().phoneNumber.toString() == frame.phoneNumber.toString()
        println("🟣 [VM/handlePresence] frame=$frame")
        println("🟣 [VM/handlePresence] snapshot.roomId=${snapshot.roomId} members.size=${snapshot.members.size} membersNames='${snapshot.membersNames}' isOnline=${snapshot.isOnline} presenceText='${snapshot.presenceText}'")
        println("🟣 [VM/handlePresence] isOneToOneForThisMember=$isOneToOneForThisMember (membersNames contains ','=${snapshot.membersNames.contains(",")}, size==1=${snapshot.members.size == 1}, phone match=${snapshot.members.firstOrNull()?.phoneNumber?.toString() == frame.phoneNumber.toString()})")

        if (isOneToOneForThisMember) {
            // Persist last-seen first (side effect) BEFORE atomic state update.
            if (frame.online) {
                println("🟣 [VM/handlePresence] -> ONLINE branch: updating DB + state")
                conversationUseCases.updateContactLastSeen(
                    lastSeen = Clock.System.now().toEpochMilliseconds(),
                    phoneNumber = frame.phoneNumber.phoneNumber,
                    countryCode = frame.phoneNumber.countryCode,
                )
                _chatState.update { current ->
                    val next = current.copy(
                        isOnline = true,
                        isTyping = false,
                        presenceText = "Online",
                    )
                    println("🟣 [VM/handlePresence] update(online) FROM presenceText='${current.presenceText}' isOnline=${current.isOnline} -> presenceText='${next.presenceText}' isOnline=${next.isOnline}")
                    next
                }
            } else {
                println("🟣 [VM/handlePresence] -> OFFLINE branch: updating DB + state, lastSeen=${frame.lastSeen}")
                conversationUseCases.updateContactLastSeen(
                    lastSeen = frame.lastSeen,
                    phoneNumber = frame.phoneNumber.phoneNumber,
                    countryCode = frame.phoneNumber.countryCode,
                )
                val formatted = conversationUseCases.formateTime(frame.lastSeen)
                println("🟣 [VM/handlePresence] formatted lastSeen='$formatted'")
                _chatState.update { current ->
                    val next = current.copy(
                        lastSeen = formatted,
                        isOnline = false,
                        isTyping = false,
                        presenceText = "lastSeen at $formatted",
                    )
                    println("🟣 [VM/handlePresence] update(offline) FROM presenceText='${current.presenceText}' isOnline=${current.isOnline} -> presenceText='${next.presenceText}' isOnline=${next.isOnline}")
                    next
                }
            }
        } else if (frame.roomId == snapshot.roomId) {
            println("🟣 [VM/handlePresence] -> GROUP branch (same roomId): clearing typing/online, presenceText=membersNames")
            _chatState.update { current ->
                val next = current.copy(
                    isOnline = false,
                    isTyping = false,
                    presenceText = current.membersNames,
                )
                println("🟣 [VM/handlePresence] update(group) FROM presenceText='${current.presenceText}' -> presenceText='${next.presenceText}'")
                next
            }
        } else {
            println("🟣 [VM/handlePresence] -> IGNORED: not for this room (frame.roomId='${frame.roomId}', chat.roomId='${snapshot.roomId}') and not 1-to-1 match")
        }
    }

    private fun handleTyping(frame: Typing) {
        val snapshot = _chatState.value
        println("🟡 [VM/handleTyping] frame=$frame")
        println("🟡 [VM/handleTyping] snapshot.roomId=${snapshot.roomId} membersNames='${snapshot.membersNames}' isOnline=${snapshot.isOnline} presenceText='${snapshot.presenceText}'")
        if (frame.roomId != snapshot.roomId) {
            println("🟡 [VM/handleTyping] -> IGNORED (roomId mismatch)")
            return
        }

        if (!snapshot.membersNames.contains(",")) {
            // 1-to-1 chat.
            if (frame.isTyping) {
                println("🟡 [VM/handleTyping] -> 1:1 TYPING=true: presenceText='typing...'")
                _chatState.update { current ->
                    val next = current.copy(isTyping = true, isOnline = true, presenceText = "typing...")
                    println("🟡 [VM/handleTyping] update(typing) FROM '${current.presenceText}' -> '${next.presenceText}'")
                    next
                }
            } else {
                println("🟡 [VM/handleTyping] -> 1:1 TYPING=false (defensive: only restore 'Online' if we already believe online; current.isOnline=${snapshot.isOnline})")
                _chatState.update { current ->
                    val next = if (current.isOnline) {
                        current.copy(isTyping = false, presenceText = "Online")
                    } else {
                        current.copy(isTyping = false)
                    }
                    println("🟡 [VM/handleTyping] update(stop-typing) FROM presenceText='${current.presenceText}' isOnline=${current.isOnline} -> presenceText='${next.presenceText}' isOnline=${next.isOnline}")
                    next
                }
            }
        } else {
            println("🟡 [VM/handleTyping] -> GROUP chat: clear typing flag only")
            _chatState.update { current ->
                current.copy(isTyping = false)
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────────
    //   Messages / ChatOpened — derived in the ViewModel against the LIVE state
    // ────────────────────────────────────────────────────────────────────────────

    private fun startObservingMessages(roomId: String) {
        println("🟢 [VM] startObservingMessages(roomId=$roomId) — (re)subscribing to chat socket events")
        observeMessageJob?.cancel()
        observeMessageJob = viewModelScope.launch {
            conversationUseCases.observeMessage().collect { frame ->
                println("🟢 [VM/observeMessage.collect] received frame=$frame (type=${frame?.let { it::class.simpleName }})")
                when (frame) {
                    is Message -> handleIncomingMessage(frame, roomId)
                    is ChatOpened -> handleChatOpened(frame, roomId)
                    is List<*> -> handleChatOpenedList(frame, roomId)
                    else -> println("🟢 [VM/observeMessage.collect] -> IGNORED unknown frame type")
                }
            }
        }
    }

    private suspend fun handleIncomingMessage(message: Message, roomId: String) {
        val isRead = !anyMemberOffline.value
        println("🟢 [VM/handleIncomingMessage] anyMemberOffline=${anyMemberOffline.value} -> isRead=$isRead, message=$message")
        conversationUseCases.saveIncomingMessage(
            message = message.copy(isRead = isRead),
            roomId = roomId,
        )
    }

    private suspend fun handleChatOpened(frame: ChatOpened, roomId: String) {
        println("🟢 [VM/handleChatOpened] frame=$frame, roomId=$roomId, current members=${_chatState.value.members}")
        // If any participant (1:1 or group) has the chat open, our messages are read.
        if (frame.chatOpened) {
            println("🟢 [VM/handleChatOpened] chatOpened=true → markMessagesAsRead")
            conversationUseCases.markMessagesAsRead(frame.roomId)
        }

        val newState = _chatState.updateAndGet { current ->
            val merged = current.members.map { existing ->
                if (existing.phoneNumber.phoneNumber == frame.phoneNumber.phoneNumber &&
                    existing.phoneNumber.countryCode == frame.phoneNumber.countryCode
                ) existing.copy(isOnline = frame.chatOpened)
                else existing
            }
            current.copy(members = merged)
        }
        anyMemberOffline.value = newState.members.any { !it.isOnline }
        println("🟢 [VM/handleChatOpened] merged members=${newState.members}, anyMemberOffline=${anyMemberOffline.value}")
    }

    private fun handleChatOpenedList(frame: List<*>, roomId: String) {
        val opens = frame.filterIsInstance<ChatOpened>()
        println("🟢 [VM/handleChatOpenedList] frame.size=${frame.size}, ChatOpened items=${opens.size}, list=$opens")
        if (opens.isEmpty()) {
            println("🟢 [VM/handleChatOpenedList] -> IGNORED (no ChatOpened items)")
            return
        }

        val newState = _chatState.updateAndGet { current ->
            val merged = current.members.map { existing ->
                val online = opens.any { evt ->
                    evt.roomId == roomId &&
                            evt.phoneNumber.phoneNumber == existing.phoneNumber.phoneNumber &&
                            evt.phoneNumber.countryCode == existing.phoneNumber.countryCode &&
                            evt.chatOpened
                }
                existing.copy(isOnline = online)
            }
            current.copy(members = merged)
        }
        anyMemberOffline.value = newState.members.any { !it.isOnline }
        println("🟢 [VM/handleChatOpenedList] merged members=${newState.members}, anyMemberOffline=${anyMemberOffline.value}")
    }

    // ────────────────────────────────────────────────────────────────────────────

    private fun handleEnterMessage(text: String) {
        _message.value = text
        val roomId = currentRoomId ?: return
        viewModelScope.launch {
            conversationUseCases.sendMemberInfo(
                roomId = roomId,
                sendPresence = false,
                isTyping = text.isNotEmpty()
            )
        }
    }

    private fun handleSendMessage() {
        val text = _message.value
        if (text.isBlank()) return
        val roomId = currentRoomId
        _message.value = ""
        viewModelScope.launch {
            conversationUseCases.sendMessage(text)
            roomId?.let {
                conversationUseCases.sendMemberInfo(
                    roomId = it,
                    sendPresence = false,
                    isTyping = false
                )
            }
        }
    }

    private fun handleChatOpen() {
        val roomId = currentRoomId ?: run {
            println("🟠 [VM/handleChatOpen] -> SKIP (currentRoomId == null)")
            return
        }
        println("🟠 [VM/handleChatOpen] roomId=$roomId, chatSocketInitializedFor=$chatSocketInitializedFor")
        viewModelScope.launch {
            val members = _chatState.value.members.map { it.phoneNumber.toString() }
            println("🟠 [VM/handleChatOpen] _chatState members=$members")

            // Re-open the chat socket ONLY if it's not already open for this room.
            // Cases:
            //  - Fresh load: GetChatState already opened it → flag set → skip.
            //  - App backgrounded + foregrounded: handleChatClose cleared the flag
            //    → open it again here. This is what makes the conversation receive
            //    messages again after the user returns to it.
            if (members.isNotEmpty() && chatSocketInitializedFor != roomId) {
                println("🟠 [VM/handleChatOpen] -> initChatSession (members != empty && flag != roomId)")
                conversationUseCases.initChatSession(members, viewModelScope)
                chatSocketInitializedFor = roomId
            } else {
                println("🟠 [VM/handleChatOpen] -> SKIP initChatSession (members.empty=${members.isEmpty()}, flag==roomId=${chatSocketInitializedFor == roomId})")
            }

            conversationUseCases.chatOpen(roomId)
            conversationUseCases.markMessagesAsRead(roomId)

            // Send presence TWICE with a delay between the two attempts.
            // The server's `sendPresence` handler replies with the current online
            // status of every other member — so this is the mechanism that refreshes
            // the top bar after the app foregrounds, even when the server fails to
            // push an offline event on its own.
            //
            // The first send may no-op if the presence socket is still re-connecting
            // (which happens immediately after `AppLifecycleObserver.onStart`).
            // The second send lands once the socket is up.
            conversationUseCases.sendMemberInfo(
                roomId = roomId,
                sendPresence = true,
                isTyping = false
            )
            delay(1000L)
            conversationUseCases.sendMemberInfo(
                roomId = roomId,
                sendPresence = true,
                isTyping = false
            )
        }

        // Start (or restart) the periodic presence-sync loop while the conversation is
        // visible. This shrinks the worst-case "other user is shown as Online but is
        // actually offline" window from ~the server's broadcast latency down to one poll.
        presencePollingJob?.cancel()
        println("🟠 [VM/handleChatOpen] starting presence-polling loop every ${PRESENCE_POLL_MILLIS}ms for roomId=$roomId")
        presencePollingJob = viewModelScope.launch {
            while (true) {
                delay(PRESENCE_POLL_MILLIS)
                println("🟠 [VM/presencePoll] tick → sendMemberInfo(roomId=$roomId, sendPresence=true)")
                conversationUseCases.sendMemberInfo(
                    roomId = roomId,
                    sendPresence = true,
                    isTyping = false
                )
            }
        }
    }

    private fun handleChatClose() {
        val roomId = currentRoomId ?: run {
            println("⚫ [VM/handleChatClose] -> SKIP (currentRoomId == null)")
            return
        }
        println("⚫ [VM/handleChatClose] roomId=$roomId, will cancel polling + close chat socket")
        viewModelScope.launch {
            conversationUseCases.sendClosedChatMsg(roomId)
            conversationUseCases.chatClose()
            // Clear the init flag so the next ChatOpen reopens the socket.
            chatSocketInitializedFor = null
            println("⚫ [VM/handleChatClose] chatSocketInitializedFor cleared")
        }
        // Stop the presence polling — we don't need fresh status while backgrounded.
        presencePollingJob?.cancel()
        presencePollingJob = null
        println("⚫ [VM/handleChatClose] presencePollingJob cancelled")
    }

    override fun onCleared() {
        observeMemberInfosJob?.cancel()
        observeMessageJob?.cancel()
        presencePollingJob?.cancel()
        super.onCleared()
    }

    private companion object {
        /** How often to re-sync presence with the server while a conversation is open. */
        const val PRESENCE_POLL_MILLIS = 8_000L

        /**
         * Per-step ceiling for the suspend chain in `handleGetChatState`. If any step
         * blows past this the VM surfaces an error instead of leaving the screen on
         * an indefinite loading spinner. 5s is long enough to absorb a slow DB read
         * over a populated members table, short enough that the user sees a failure.
         */
        const val LOAD_STEP_TIMEOUT_MILLIS = 5_000L
    }
}
