package com.example.chatter.core.data.data_source.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.model.remote.ContactInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UserServiceImp(
    private val client : HttpClient
) : UserService {
    override suspend fun getContactInfo(phoneNumber: PhoneNumber): Result<ContactInfo> {
            println("\uD83D\uDE1B [UserServiceImp] getFullContact")
            return runCatching {
                val response = client.post {
                    url(UserService.EndPoints.UserInfo.url)
                    setBody(
                        phoneNumber
                    )
                    contentType(ContentType.Application.Json)
                }
                println("😛 [UserServiceImp] getFullContact body ${response.status}")
                when (response.status) {
                    HttpStatusCode.OK -> {
                        val contactInfo = response.body<ContactInfo>()
                        println("😛 [UserServiceImp] getFullContact , contactInfo $contactInfo ")
                        contactInfo
                    }
                    HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                    else -> throw Exception("Unexpected error")
                }
            }
    }
}