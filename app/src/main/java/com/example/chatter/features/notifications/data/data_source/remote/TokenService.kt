package com.example.chatter.features.notifications.data.data_source.remote

import com.example.chatter.Constants.BASE_URL

interface TokenService {
    sealed class EndPoints(val url : String){
        object SaveToken  : EndPoints("$BASE_URL/save_fcm_token")
        object UpdateToken : EndPoints("$BASE_URL/update_fcm_token")
    }
}