package com.example.chatter.features.chats.domain.use_case.conversation

import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.chats.presentation.conversation.model.Member

class GetSenderName{
    operator fun invoke(
        members: List<Member>,
        number: PhoneNumber
    ): String {
        return members.find { (phoneNumber, name) ->
            number.countryCode == phoneNumber.countryCode
                    && number.phoneNumber == phoneNumber.phoneNumber
        }?.name ?: "You"
    }
}