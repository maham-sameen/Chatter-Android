package com.example.chatter.features.auth.data.data_source.remote

import com.example.chatter.Constants.BASE_URL

interface SetUpService {
    sealed class EndPoints(val url : String){
        object GetUserRelatedData : EndPoints("${BASE_URL}/get_user_related_data")
    }
}