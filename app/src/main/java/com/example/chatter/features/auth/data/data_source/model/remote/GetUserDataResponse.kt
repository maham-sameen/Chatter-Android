package com.example.chatter.features.auth.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.remote.ChatRoomResponse
import kotlinx.serialization.Serializable

@Serializable
data class GetUserDataResponse(
    val contacts : List<UserInfo> ,
    val members : List<Member> ,
    val chatRooms : List<ChatRoomResponse>
)
