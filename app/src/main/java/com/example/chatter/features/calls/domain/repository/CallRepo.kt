package com.example.chatter.features.calls.domain.repository

import androidx.paging.PagingData
import com.example.chatter.features.calls.domain.model.ChatterCall
import kotlinx.coroutines.flow.Flow

interface CallRepo {

    fun getCallHistory(userId : String) : Flow<PagingData<ChatterCall>>
    fun getCurrentUserId() : Flow<String>

}