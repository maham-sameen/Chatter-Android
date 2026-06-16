package com.example.chatter.features.calls.di

import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.features.calls.domain.repository.CallRepo
import com.example.chatter.features.calls.domain.use_case.CallHistoryUseCase
import com.example.chatter.features.calls.domain.use_case.GetCallHistory
import com.example.chatter.features.calls.presentation.call_history.CallHistoryViewModel
import com.example.chatter.core.domain.use_case.FormateTime
import com.example.chatter.features.calls.domain.use_case.GetCurrentUserId
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val callViewModelModule = module {
    scope<CallHistoryViewModel>{
        scoped<CallHistoryUseCase> {
            CallHistoryUseCase(
                getCallHistory = GetCallHistory(
                    callRepo = get<CallRepo>(),
                    getRandomColor = get<GetRandomColor>(),
                    formateTime = get<FormateTime>()
                ),
                getCurrentUserId = GetCurrentUserId(callRepo = get<CallRepo>())
            )
        }
    }
    viewModelOf(::CallHistoryViewModel)
}