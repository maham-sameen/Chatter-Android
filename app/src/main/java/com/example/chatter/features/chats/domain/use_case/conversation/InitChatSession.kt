package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo
import kotlinx.coroutines.CoroutineScope

class InitChatSession(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(
        members : List<String> ,
        scope : CoroutineScope
    ){
        println("🛠️ [UC/InitChatSession] members=$members")
        val result = chatRepo.initSession(members , scope)
        println("🛠️ [UC/InitChatSession] result.isSuccess=${result.isSuccess} error=${result.exceptionOrNull()?.message}")
    }
}