package com.example.chatter.features.calls.data.data_source.remote

import com.example.chatter.Constants
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.calls.data.data_source.model.remote.CreateUserRequest
import com.example.chatter.features.calls.data.data_source.model.remote.User

interface UserService {

    suspend fun saveUser(createUserRequest: CreateUserRequest) : Result<Boolean>
    suspend fun getUser(phoneNumber: PhoneNumber) : Result<User>
    suspend fun loadToken(phoneNumber: PhoneNumber) : Result<String>

    sealed class EndPoints(val url : String){
        data object SaveUser : EndPoints("${Constants.BASE_URL}/save_user")
        data object GetUser : EndPoints("${Constants.BASE_URL}/get_user")
        data object LoadToken : EndPoints("${Constants.BASE_URL}/load_token")
    }
}