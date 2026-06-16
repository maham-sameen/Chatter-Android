package com.example.chatter.features.chats.domain.use_case.chats

import com.example.chatter.features.chats.domain.repository.ChatRepo

class IsSessionExpired (
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(): Boolean{
        return !chatRepo.isTokenValid()
    }
}