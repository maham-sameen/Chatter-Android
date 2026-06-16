package com.example.chatter.features.calls.domain.use_case

import com.example.chatter.features.calls.domain.repository.CallRepo
import kotlinx.coroutines.flow.Flow

class GetCurrentUserId(
    private val callRepo: CallRepo
) {
    operator fun invoke(): Flow<String>{
        return callRepo.getCurrentUserId()
    }
}