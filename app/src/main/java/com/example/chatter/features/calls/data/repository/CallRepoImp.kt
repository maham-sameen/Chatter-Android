package com.example.chatter.features.calls.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.remote.UserSession
import com.example.chatter.features.calls.data.data_source.mapper.ChatterCallDbToChatterCall
import com.example.chatter.features.calls.data.data_source.remote.CallRemoteDataSource
import com.example.chatter.features.calls.data.paging_source.CallHistoryPagingSource
import com.example.chatter.features.calls.domain.model.ChatterCall
import com.example.chatter.features.calls.domain.repository.CallRepo
import com.example.chatter.features.chats.data.data_source.remote.ChatSocketService
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class CallRepoImp(
    private val callRemoteDataSource: CallRemoteDataSource  ,
    private val chatterCallDbToChatterCall: ChatterCallDbToChatterCall ,
    private val chatSocketService: ChatSocketService ,
) : CallRepo {
    @OptIn(ExperimentalPagingApi::class)
    override fun getCallHistory(userId : String): Flow<PagingData<ChatterCall>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 1,
                enablePlaceholders = true,
                initialLoadSize = 10,
            ) ,
            pagingSourceFactory = {
                CallHistoryPagingSource(
                    callRemoteDataSource = callRemoteDataSource,
                    userId = userId
                )
            } ,
        ).flow.map {
                pagingData ->
            println("\uD83E\uDD3C\u200D♂\uFE0F  [CallRepoImp] , pagingData : ${pagingData.toString()}")
            pagingData.map{
                    chatterCall ->
                println("\uD83E\uDD3C\u200D♂\uFE0F  [CallRepoImp] , chatterCall : $chatterCall")
                chatterCallDbToChatterCall.map(chatterCall)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override  fun getCurrentUserId(): Flow<String> {
        return StreamUserDataStore.instance().user
            .filterNotNull()
            .mapLatest {
                println("\uD83E\uDD3C\u200D♂\uFE0F  [CallRepoImp] , getCurrentUserId : ${it.toString()}")
                it.id
            }
    }
}