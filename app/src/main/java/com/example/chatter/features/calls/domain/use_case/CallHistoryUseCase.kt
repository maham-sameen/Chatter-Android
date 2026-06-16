package com.example.chatter.features.calls.domain.use_case

data class CallHistoryUseCase(
    val getCallHistory: GetCallHistory ,
    val getCurrentUserId: GetCurrentUserId
)
