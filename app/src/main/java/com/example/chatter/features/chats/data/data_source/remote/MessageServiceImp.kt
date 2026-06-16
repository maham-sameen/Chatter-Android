package com.example.chatter.features.chats.data.data_source.remote

import com.example.chatter.features.chats.data.data_source.model.remote.PaginatedRequest
import com.example.chatter.features.chats.data.data_source.model.remote.PaginatedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class MessageServiceImp(
    private val client : HttpClient
) : MessageService {
    override suspend fun getAllMessages(paginatedRequest: PaginatedRequest): Result<PaginatedResponse> {
        println("\uD83D\uDE1B MessageServiceImp getAllMessages")
        return runCatching {
            val response = client.post {
                url(MessageService.EndPoints.GetMessagesPaged.url)
                setBody(paginatedRequest)
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B MessageServiceImp getAllMessages $body")
            when (response.status) {
                HttpStatusCode.OK -> {
                    val paginatedResponse = Json.decodeFromString<PaginatedResponse>(body)
                    println("\uD83D\uDE1B MessageServiceImp getAllMessages paginatedResponse : $paginatedResponse")
                    paginatedResponse
                }
                HttpStatusCode.Conflict -> throw Exception("Fields are blank or invalid")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }
}