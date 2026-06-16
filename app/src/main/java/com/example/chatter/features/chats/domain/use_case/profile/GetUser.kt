package com.example.chatter.features.chats.domain.use_case.profile

import androidx.compose.runtime.mutableStateListOf
import com.example.chatter.R
import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.features.chats.domain.repository.ProfileRepo
import com.example.chatter.features.chats.presentation.profile.model.ProfileState
import com.example.chatter.features.chats.presentation.profile.model.UserDetail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetUser(
    private val profileRepo: ProfileRepo ,
    private  val fromHexCode: FromHexCode ,
    private val getRandomColor: GetRandomColor
) {

    @OptIn(ExperimentalCoroutinesApi::class)
     operator fun invoke(): Flow<ProfileState>{
        println("\uD83E\uDD25  GetUser Profile ")
        val list = mutableStateListOf<UserDetail>()
        return profileRepo.getUser().mapLatest {
            println("\uD83E\uDD25  GetUser Profile ${it.toString()} ")
            ProfileState(
                image = it.image,
                colorScheme = it.colorScheme?.let{ fromHexCode(it) } ?: getRandomColor(),
                userDetails =list.apply {
                    clear()
                    addAll(
                        listOf(
                            UserDetail(
                                name = "Name",
                                icon = R.drawable.baseline_person_24,
                                value = it.name
                            ),
                            UserDetail(
                                name = "Phone",
                                icon = R.drawable.outline_call_24,
                                value = "${it.phoneNumber.countryCode}${it.phoneNumber.phoneNumber}"
                            )
                        )
                    )
                }
            )
        }
    }
}