package com.example.chatter.features.calls.data.data_source.model.remote

import kotlinx.serialization.Serializable

// 🧾 Model class matching your backend JSON response
@Serializable
data class GetAuthDataResponse(
    val userId: String,
    val apiKey: String,
    val token: String
)