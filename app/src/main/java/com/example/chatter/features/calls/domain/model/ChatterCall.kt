package com.example.chatter.features.calls.domain.model


data class ChatterCall(
    val cid: String,
    val members: List<CallMember>,
    val status: CallStatus,
    val timeStamp: Long,
)