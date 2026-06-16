package com.example.chatter.features.chats.data.data_source.remote

import com.example.chatter.Constants.BASE_URL

interface UserService {
    sealed class EndPoints(val url : String){
        object UpdateUserProfile : EndPoints("${BASE_URL}/updated_user_profile")
    }
}