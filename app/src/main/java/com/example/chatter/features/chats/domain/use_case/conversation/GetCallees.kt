package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo

class GetCallees(
    private val chatRepo: ChatRepo
) {

    suspend operator fun invoke(
        members  : List<String>
    ) : List<String>{
        println("\uD83C\uDF19 Conversation UseCases GetCallees , members : $members")
        val currentUser = chatRepo.getUserPhoneNumber()
        val callees =  mutableListOf<String>()
            .apply {
                addAll(members)
            }
        callees.add(currentUser)
        println("\uD83C\uDF19 Conversation UseCases GetCallees , callees : $callees")
        return callees
    }
}