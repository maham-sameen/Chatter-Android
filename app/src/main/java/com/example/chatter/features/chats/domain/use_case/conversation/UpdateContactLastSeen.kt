package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.features.chats.domain.repository.ChatRepo

class UpdateContactLastSeen(
    private val chatRepo: ChatRepo
) {
    suspend operator fun invoke(
        lastSeen: Long,
        phoneNumber: String,
        countryCode: String
    ): Int {
        println("📒 [UC/UpdateContactLastSeen] phone=$countryCode-$phoneNumber lastSeen=$lastSeen")
        val updated = chatRepo.updateContactLastSeen(
            lastSeen = lastSeen,
            phoneNumber = phoneNumber,
            countryCode = countryCode
        )
        println("📒 [UC/UpdateContactLastSeen] rows updated = $updated")
        return updated
    }
}
