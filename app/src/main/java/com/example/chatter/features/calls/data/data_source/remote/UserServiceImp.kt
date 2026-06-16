package com.example.chatter.features.calls.data.data_source.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.calls.data.data_source.model.remote.CreateUserRequest
import com.example.chatter.features.calls.data.data_source.model.remote.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class UserServiceImp(
    private val client :  HttpClient
) : UserService {
    override suspend fun saveUser(
        createUserRequest: CreateUserRequest
    ): Result<Boolean> {
        println("\uD83D\uDEF4 [UserServiceImp] , saveUser : createUserRequest : $createUserRequest ,")
        return runCatching {
            val response = client.post {
                url(UserService.EndPoints.SaveUser.url)
                setBody(createUserRequest)
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B [UserServiceImp] Call UserService Imp  body $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val result = Json.decodeFromString<Boolean>(body)
                    if(result){
                       true
                    }else{
                        throw Exception("Can not insert the call user")
                    }
                }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }

    override suspend fun getUser(phoneNumber: PhoneNumber): Result<User> {
        println("\uD83D\uDEF4 [UserServiceImp]  getUser , phoneNumber : $phoneNumber")
        return runCatching {
            val response = client.post {
                url(UserService.EndPoints.GetUser.url)
                setBody(phoneNumber)
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B [UserServiceImp] Imp getUser  body $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                   Json.decodeFromString<User>(body)
                }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }

    override suspend fun loadToken(phoneNumber: PhoneNumber): Result<String> {
        println("\uD83D\uDEF4 Call [UserServiceImp]   , loadToken , phoneNumber : $phoneNumber")
        return runCatching {
            val response = client.post {
                url(UserService.EndPoints.LoadToken.url)
                setBody(phoneNumber)
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B Call [UserServiceImp] loadToken  body $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                   body
                }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }
}