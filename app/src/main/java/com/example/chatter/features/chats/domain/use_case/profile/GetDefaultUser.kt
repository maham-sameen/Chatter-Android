package com.example.chatter.features.chats.domain.use_case.profile

import androidx.compose.runtime.mutableStateListOf
import com.example.chatter.R
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.features.chats.presentation.profile.model.ProfileState
import com.example.chatter.features.chats.presentation.profile.model.UserDetail

class GetDefaultUser(
    private val getRandomColor: GetRandomColor
) {

    operator fun invoke(): ProfileState{
        return ProfileState(
            image = null,
            colorScheme = getRandomColor(),
            userDetails = mutableStateListOf<UserDetail>().apply {
                clear()
                addAll(listOf(
                    UserDetail(
                        name = "Name",
                        icon = R.drawable.baseline_person_24,
                        value = "You"
                    ) ,
                    UserDetail(
                        name = "Phone",
                        icon =  R.drawable.outline_call_24,
                        value = ""
                    )
                ))
            }
        )
    }
}