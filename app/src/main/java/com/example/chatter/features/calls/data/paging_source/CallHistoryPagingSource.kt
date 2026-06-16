package com.example.chatter.features.calls.data.paging_source

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.chatter.features.calls.data.data_source.model.local.ChatterCall
import com.example.chatter.features.calls.data.data_source.remote.CallRemoteDataSource


class CallHistoryPagingSource(
    private val userId : String ,
    private val callRemoteDataSource: CallRemoteDataSource , 
) : PagingSource<String, ChatterCall>() {
    override fun getRefreshKey(state: PagingState<String, ChatterCall>): String? {
        println("📌[CallHistoryPagingSource] getRefreshKey CALLED")
        println(" [CallHistoryPagingSource] config: ${state.config}")
        println(" [CallHistoryPagingSource] pages: ${state.pages}")
        println(" [CallHistoryPagingSource] anchorPosition: ${state.anchorPosition}")
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey
                ?: state.closestPageToPosition(anchor)?.nextKey
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(params: LoadParams<String>): LoadResult<String, ChatterCall> {
        println("📌 [CallHistoryPagingSource]  load CALLED")
        println(" [CallHistoryPagingSource]  params.key (page number): ${params.key}")
        println(" [CallHistoryPagingSource]  params.loadSize (page size): ${params.loadSize}")

        return try {
            val pageNumber = params.key
            val pageSize = params.loadSize

            println("[CallHistoryPagingSource]  -> Fetching pageNumber: $pageNumber, pageSize: $pageSize ")

            val calls = callRemoteDataSource.getRecentCalls(
                limit = 10,
                prev = pageNumber,
                next = pageNumber,
                userId = userId
            )
            println(" [CallHistoryPagingSource] -> API returned Calls: ${calls.chatterCall.toString()}")

            LoadResult.Page(
                data = calls.chatterCall,
                prevKey = calls.prev,
                nextKey = calls.next
            )
        } catch (e: Exception) {
            println("⚠️[CallHistoryPagingSource] Exception in load(): $e")
            LoadResult.Error(e)
        }
    }
}