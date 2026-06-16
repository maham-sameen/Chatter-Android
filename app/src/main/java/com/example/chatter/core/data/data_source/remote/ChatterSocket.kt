package com.example.chatter.core.data.data_source.remote

import io.ktor.websocket.WebSocketSession

object ChatterSocket {
    var presenceSocket : WebSocketSession? = null
    var chatSocket : WebSocketSession? = null
}