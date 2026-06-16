package com.example.chatter.features.chats.presentation.conversation.model

import java.util.UUID

data class MemberList(
    val list : List<Member> = emptyList(),
    private val id: UUID = UUID.randomUUID(),//added unique id
)
