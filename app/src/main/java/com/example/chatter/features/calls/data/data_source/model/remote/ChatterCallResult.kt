package com.example.chatter.features.calls.data.data_source.model.remote

import com.example.chatter.features.calls.data.data_source.model.local.ChatterCall

data class ChatterCallResult(
    val chatterCall : List<ChatterCall> = emptyList(),
    val prev : String? = null,
    val next : String? = null
)
