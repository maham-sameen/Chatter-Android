package com.example.chatter.features.chats.data.data_source.model.remote

import kotlinx.serialization.Serializable


@Serializable
data class PaginatedResponse(
    val page: Int,
    val pageSize: Int,
    val totalCount: Long,
    val totalPages: Int,
    val data: List<Message>
)
