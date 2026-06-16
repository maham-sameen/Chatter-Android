package com.example.chatter.core.data.data_source.remote

import com.example.chatter.Constants.BASE_URL
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.model.remote.ContactInfo

interface UserService {

    suspend fun getContactInfo(
        phoneNumber : PhoneNumber
    ) : Result<ContactInfo>

    sealed class EndPoints(val url : String){
        object Authenticate : EndPoints("$BASE_URL/authenticate")
        object GetAllUsers : EndPoints("${BASE_URL}/users")
        object UserInfo : EndPoints("${BASE_URL}/get_user_info")
    }
}
