package com.example.chatter.core.data.data_source.remote

import com.example.chatter.core.data.data_source.mapper.UserDtoToUser
import com.example.chatter.core.data.data_source.model.remote.UserDto
import com.example.chatter.core.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UsersDataStore (
    private val client: HttpClient ,
    private val userDtoToUser: UserDtoToUser
){
    suspend fun getAllUsers(): Result<List<User>> {
        println("\uD83D\uDE1B getAllUsers")
        return runCatching {
            val response = client.get {
                url(UserService.EndPoints.GetAllUsers.url)
                contentType(io.ktor.http.ContentType.Application.Json)
            }
            val body = response.body<List<UserDto>>()
            println("\uD83D\uDE1B  getAllUsers body $body")
            when (response.status) {
                HttpStatusCode.OK -> body.map { userDtoToUser.map(it) }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }
}