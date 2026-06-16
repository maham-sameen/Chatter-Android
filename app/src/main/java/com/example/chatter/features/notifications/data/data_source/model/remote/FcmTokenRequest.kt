package com.example.chatter.features.notifications.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import kotlinx.serialization.Serializable


@Serializable
data class FcmTokenRequest(
    val phoneNumber: PhoneNumber,
    val fcmToken: String
)