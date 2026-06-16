package com.example.chatter.features.calls.data.data_source.model.local

data class ChatterCall(
    val cid: String,
    val members: List<CallMember>,
    val status: CallStatus,
    val timeStamp: Long,
)