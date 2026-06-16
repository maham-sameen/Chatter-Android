package com.example.chatter.features.chats.data.data_source.remote

import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.core.data.data_source.remote.ChatterSocket
import com.example.chatter.features.chats.data.data_source.mapper.ChatOpenedDbToChatOpened
import com.example.chatter.features.chats.data.data_source.mapper.MessageDtoToMessage
import com.example.chatter.features.chats.data.data_source.model.remote.ChatOpened
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json


class ChatSocketServiceImp(
    private val client : HttpClient ,
    private val chatOpenedDbToChatOpened: ChatOpenedDbToChatOpened
) : ChatSocketService
{

    init {
        println("🔥 ChatSocketServiceImp INSTANCE: $this")
    }

    private var listeningJob: Job? = null



    private val _messages = MutableSharedFlow<Any>(
        replay =  1,                 // ✅ stores only latest
        extraBufferCapacity = 64    // ✅ NO backlog
    )
    val messages: SharedFlow<Any> = _messages
    override suspend fun initSession(userSession: UserSession , scope : CoroutineScope): Result<Unit> {
        return try {
            println("\uD83D\uDE08 [ChatSocketServiceImp] initSession userSession : ${userSession.toString()}")
            closeSession()
            ChatterSocket.chatSocket = null

            // ✅ 2. LOG COOKIES BEFORE CONNECT
            val cookiesBefore = client.cookies("http://10.0.2.2:8082")
            println("🍪[ChatSocketServiceImp] Cookies BEFORE new socket: $cookiesBefore")

            // ✅ 3. CREATE NEW SOCKET SESSION
            println("🚀[ChatSocketServiceImp] Creating NEW socket session...")
            ChatterSocket.chatSocket = client.webSocketSession {
                url("${ChatSocketService.EndPoints.ChatSocket.url}?user_phone=${userSession.userPhone}&contact_phone=${userSession.contactPhone.joinToString(",")}")
            }
            println("🍪 [ChatSocketServiceImp] Client cookies after GET: " + client.cookies("http://10.0.2.2:8082"))

            if(ChatterSocket.chatSocket?.isActive == true){
                println("\uD83D\uDE08 [ChatSocketServiceImp] we have connected successfully")
                startListening(scope)
                Result.success(Unit)
            }else{
                println("\uD83D\uDE08[ ChatSocketServiceImp] we have connected failed")
                Result.failure(Exception("Could not establish a connection"))
            }
        }catch (e : Exception){
            println("\uD83D\uDE08 [ChatSocketServiceImp] catch ${e.toString()}")
            e.printStackTrace()
            if(e is CancellationException){
                throw e
            }
            with(NonCancellable){ Result.failure(e) }
        }
    }

    override suspend fun closeSession() {
        println("🛑 [ChatSocketServiceImp] closeSession CALLED")

        // Snapshot + null the global field first so nobody races in.
        val ws = ChatterSocket.chatSocket
        ChatterSocket.chatSocket = null

        // STEP 1: send the WebSocket CLOSE frame BEFORE cancelling anything.
        //
        // Same bug as the presence socket: the previous code closed
        // `socket.outgoing` before `socket.close()`, which meant the close frame
        // was never written. The server only learned the chat socket was gone
        // via its ping timeout, so the peer's `chatOpened=false` notification
        // was delayed too. Send the close frame FIRST.
        ws?.let { socket ->
            try {
                println("🛑 [ChatSocketServiceImp] sending CLOSE frame | isActive=${socket.isActive}")
                socket.close(
                    CloseReason(
                        CloseReason.Codes.NORMAL,
                        "Client closing"
                    )
                )
                println("✅ [ChatSocketServiceImp] CLOSE frame sent")
            } catch (e: Exception) {
                println("⚠️ [ChatSocketServiceImp] Error sending close frame: ${e.message}")
                if (e is CancellationException) throw e
            }
        }

        // STEP 2: cancel the receive coroutine now that the socket is closed.
        listeningJob?.cancel()
        listeningJob = null

        ChatterSocket.chatSocket = null
        println("✅ [ChatSocketServiceImp] Socket fully cleared")
    }

    override suspend fun sendMessage(json: String) {
        try {
            println("\uD83E\uDDF6 [ChatSocketServiceImp] , sendMessage : $json")
            ChatterSocket.chatSocket?.send(frame = Frame.Text(json))
        }catch (e : Exception){
            e.printStackTrace()
            println("\uD83E\uDDF6 [ChatSocketServiceImp] , e ${e.toString()}")
            if(e is CancellationException){
                throw e
            }
        }
    }

    override fun observerMessage(): SharedFlow<Any?> {
        return messages
    }


    private fun startListening(scope : CoroutineScope){
       println("[ChatSocketServiceImp] startListening ")
        val messageDtoToMessage = MessageDtoToMessage()
        listeningJob?.cancel() // ✅ kill any previous

        listeningJob = scope.launch{
            println("[ChatSocketServiceImp] startListening  scope launch")
            try{
                ChatterSocket.chatSocket?.incoming
                    ?.receiveAsFlow()
                    ?.filter {
                        it is Frame.Text
                    }
                    ?.mapNotNull {
                        val json = (it as Frame.Text).readText()
                        println("\uD83D\uDE08 [ChatSocketServiceImp] mapNotNull json : ${json.toString()}")
                        val message = try {
                            val messageDto =
                                Json.decodeFromString<com.example.chatter.features.chats.data.data_source.model.remote.Message>(
                                    json
                                )
                            messageDtoToMessage.map(messageDto)
                        } catch (e: SerializationException) {
                            try {
                                val chatOpened = Json.decodeFromString<ChatOpened>(json)
                                chatOpenedDbToChatOpened.map(chatOpened)
                            } catch (e: SerializationException) {
                                try {
                                    Json.decodeFromString<List<ChatOpened>>(json).map {
                                        chatOpenedDbToChatOpened.map(it)
                                    }
                                } catch (e: SerializationException) {
                                    println("SerializationException , e : $e")
                                }
                            }
                        }
                        println("\uD83D\uDE08 [ChatSocketServiceImp] observeMessage , message : $message")
                        println(
                            "🍪 [ChatSocketServiceImp] Client cookies after GET: " + client.cookies(
                                "http://10.0.2.2:8082"
                            )
                        )
                        message
                    }?.collect {
                        println("\uD83D\uDE08 [ChatSocketServiceImp] observeMessage , message : $it")
                        _messages.emit(it)
                    }
            }catch (e : Exception){
                println("\uD83D\uDE08 [ChatSocketServiceImp] observeMessage , e : ${e.toString()}")
                if(e is CancellationException){
                    throw e
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun clearLastChat() {
        _messages.resetReplayCache()
    }
}