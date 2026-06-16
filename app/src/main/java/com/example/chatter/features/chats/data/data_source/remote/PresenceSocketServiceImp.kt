package com.example.chatter.features.chats.data.data_source.remote



import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.core.data.data_source.remote.ChatterSocket
import com.example.chatter.features.chats.data.data_source.mapper.PresenceDbToPresence
import com.example.chatter.features.chats.data.data_source.mapper.PresenceToPresenceDb
import com.example.chatter.features.chats.data.data_source.mapper.TypingDbToTyping
import com.example.chatter.features.chats.data.data_source.mapper.TypingToTypingDb
import com.example.chatter.features.chats.data.data_source.model.remote.Presence
import com.example.chatter.features.chats.data.data_source.model.remote.Typing
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json



class PresenceSocketServiceImp(
    private val client : HttpClient ,
    private val presenceDbToPresence: PresenceDbToPresence ,
    private val typingDbToTyping: TypingDbToTyping ,
    private val presenceToPresenceDb: PresenceToPresenceDb ,
    private val typingToTypingDb: TypingToTypingDb ,
) : PresenceSocketService {

    init {
        println("init of teh Presence of socket service implementation ")
    }

    private val _memberEvents = MutableSharedFlow<Any?>(
        replay = 1,                 // ✅ stores only latest
        extraBufferCapacity = 64     // ✅ NO backlog
    )
    val memberEvents: SharedFlow<Any?> = _memberEvents

    private var listeningJob: Job? = null


    override fun observerMemberInfo(): SharedFlow<Any?> {
        println("[PresenceSocketServiceImp] observerMemberInfo ")
        return memberEvents
    }


    private fun startListening(scope: CoroutineScope) {
        println("[PresenceSocketServiceImp] startListening ")
        listeningJob?.cancel() // ✅ kill any previous
        listeningJob = scope.launch {
           println("[PresenceSocketServiceImp] startListening  scope launch ")
            try{
                ChatterSocket.presenceSocket?.incoming
                    ?.receiveAsFlow()
                    ?.filter { it is Frame.Text }
                    ?.mapNotNull { frame ->
                        val json = (frame as Frame.Text).readText()
                        println("\uD83D\uDE08 [PresenceSocketServiceImp] startListening mapNotNull json: $json")

                        try {
                            val presence = Json.decodeFromString<Presence>(json)
                            println("\uD83D\uDE08 [PresenceSocketServiceImp] startListening mapNotNull presence : ${presence.toString()}")
                            presenceDbToPresence.map(presence)
                        } catch (e: SerializationException) {
                            val typing = Json.decodeFromString<Typing>(json)
                            typingDbToTyping.map(typing)
                        }.also {
                            println("\uD83D\uDE08 [PresenceSocketServiceImp] startListening : $it")
                        }
                    }?.collect { event ->
                        println("\uD83D\uDE08 [PresenceSocketServiceImp] startListening event : $event")
                        _memberEvents.emit(event)
                    }
            }catch ( e: Exception){
                println("\uD83D\uDE08 [PresenceSocketServiceImp] startListening exception : ${e.toString()}")
                if(e is CancellationException) throw  e
            }
        }
    }



    override suspend fun sendMemberInfo(memberInfo: Any) {
        try {
            println("\uD83D\uDE08 [PresenceSocketServiceImp] sendMemberInfo : ${memberInfo.toString()}")
            // ✅ Serialize the data class to a JSON string
            val incomingFrame = when(memberInfo){
                is com.example.chatter.features.chats.domain.model.Presence ->{
                    Json.encodeToString(presenceToPresenceDb.map(memberInfo))
                }
                is com.example.chatter.features.chats.domain.model.Typing -> {
                    Json.encodeToString(typingToTypingDb.map(memberInfo))
                }
                else -> {""}
            }
            println("📤 [PresenceSocketServiceImp]  Sending JSON: $incomingFrame")
            ChatterSocket.presenceSocket?.send(Frame.Text(incomingFrame))
            println("🍪 [PresenceSocketServiceImp] Client cookies after GET: " + client.cookies("http://10.0.2.2:8082"))
        }catch (e : Exception){
            println("\uD83D\uDE08 [PresenceSocketServiceImp] exception ${e.toString()}")
            e.printStackTrace()

            if(e is CancellationException) throw  e
        }
    }

    override suspend fun initSession(userSession: UserSession , scope : CoroutineScope): Result<Unit> {
        return try {

            println("😈[UserSocketServiceImp] initSession CALLED for user: ${userSession.userPhone}")
            closeSession()
            ChatterSocket.presenceSocket = null

            // ✅ 2. LOG COOKIES BEFORE CONNECT
            val cookiesBefore = client.cookies("http://10.0.2.2:8082")
            println("🍪[UserSocketServiceImp] Cookies BEFORE new socket: $cookiesBefore")

            // ✅ 3. CREATE NEW SOCKET SESSION
            println("🚀[UserSocketServiceImp] Creating NEW socket session...")

            ChatterSocket.presenceSocket = client.webSocketSession {
                url("${PresenceSocketService.EndPoints.PresenceSocket.url}?user_phone=${userSession.userPhone}")
            }

            val newSocket = ChatterSocket.presenceSocket

            println("✅ [UserSocketServiceImp] New socket created | isActive = ${newSocket?.isActive}")
            println("🧪 [UserSocketServiceImp] New socket hash = ${newSocket?.hashCode()}")

            // ✅ 4. VERIFY CONNECTION
            if (newSocket?.isActive == true) {
                println("✅✅ [UserSocketServiceImp] WebSocket CONNECTED successfully")
                startListening(scope)
                Result.success(Unit)
            } else {
                println("❌❌ [UserSocketServiceImp] WebSocket FAILED to connect")
                Result.failure(Exception("WebSocket not active"))
            }

        } catch (e: Exception) {
            println("💥 initSession EXCEPTION: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun closeSession() {
        println("🛑 [PresenceSocketServiceImp] closeSession CALLED")

        // Snapshot the socket and null the global field IMMEDIATELY so no other
        // caller can race in and try to use this dying session.
        val ws = ChatterSocket.presenceSocket
        ChatterSocket.presenceSocket = null

        // STEP 1: send the WebSocket CLOSE frame BEFORE cancelling anything else.
        //
        // The old order closed `socket.outgoing` *before* `socket.close()`, which
        // meant the close frame could never be written → the server only learned
        // the client was gone via its ping timeout (~60s+). That's the source of
        // the "Online" lag after one user closes the app.
        //
        // Calling `socket.close(reason)` first writes the close frame and lets
        // Ktor tear down the channels cleanly. The server's tryDisconnect()
        // then fires within milliseconds.
        ws?.let { socket ->
            try {
                println("🛑 [PresenceSocketServiceImp] sending CLOSE frame | isActive=${socket.isActive}")
                socket.close(
                    CloseReason(
                        CloseReason.Codes.NORMAL,
                        "Client closing"
                    )
                )
                println("✅ [PresenceSocketServiceImp] CLOSE frame sent")
            } catch (e: Exception) {
                println("⚠️ [PresenceSocketServiceImp] Error sending close frame: ${e.message}")
                if (e is CancellationException) throw e
            }
        }

        // STEP 2: cancel the receive coroutine. It'll exit cleanly because the
        // socket is now closed; previously the order was reversed, which also
        // cancelled the context the close frame was trying to use.
        listeningJob?.cancel()
        listeningJob = null

        println("✅ [PresenceSocketServiceImp] Socket fully cleared")
    }
}