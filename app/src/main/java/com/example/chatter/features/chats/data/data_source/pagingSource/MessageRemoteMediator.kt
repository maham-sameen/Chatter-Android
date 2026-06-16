package com.example.chatter.features.chats.data.data_source.pagingSource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.chatter.core.data.data_source.local.MessageDao
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.chats.data.data_source.model.local.MessageEntity
import com.example.chatter.features.chats.data.data_source.local.MessageRemoteKeyDao
import com.example.chatter.features.chats.data.data_source.mapper.MessageToMessageEntity
import com.example.chatter.features.chats.data.data_source.model.local.MessageRemoteKey
import com.example.chatter.features.chats.data.data_source.model.remote.PaginatedRequest
import com.example.chatter.features.chats.data.data_source.remote.MessageService

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val messageRemoteKeyDao: MessageRemoteKeyDao,
    private val messageDao: MessageDao,
    private val messageService: MessageService,
    private val roomId : String,
    private val messageToMessageEntity: MessageToMessageEntity,
    private val phoneNumbers : List<PhoneNumber>
) : RemoteMediator<Int, MessageEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MessageEntity>): MediatorResult {

        println("\n============================ 📍 RemoteMediator Triggered ============================")
        println("➡️ LoadType = $loadType")
        println("➡️ Current DB last item: ${state.lastItemOrNull()}")
        println("➡️ Current DB first item: ${state.firstItemOrNull()}")
        println("➡️ anchorPosition = ${state.anchorPosition}")
        println("➡️ roomId = $roomId")

        val page = when(loadType){

            LoadType.REFRESH -> {
                println("🔁 REFRESH requested...")

                val remoteKey = state.anchorPosition?.let { pos ->
                    val closestMessage = state.closestItemToPosition(pos)
                    println("🧲 Closest anchor message = $closestMessage")
                    closestMessage?.let { messageRemoteKeyDao.getRemoteKey(it.id) }
                }

                println("📦 REFRESH => remoteKey from DB = $remoteKey")

                val calculated = remoteKey?.nextKey?.minus(1) ?: 1
                println("📄 REFRESH loading page = $calculated")
                calculated
            }

            LoadType.APPEND -> {
                println("📩 APPEND requested... scrolling down")

                val remoteKey = state.pages.lastOrNull { it.data.isNotEmpty() }
                    ?.data?.lastOrNull()?.let {
                        println("🔍 Checking remote key for last item: ${it.id}")
                        messageRemoteKeyDao.getRemoteKey(it.id)
                    }

                println("📦 APPEND => remoteKey from DB = $remoteKey")

                val page = remoteKey?.nextKey
                if (page == null) {
                    println("🚫 APPEND END -> No nextKey found. End of pagination.")
                    return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                }
                println("📄 APPEND loading page = $page")
                page
            }

            LoadType.PREPEND -> {
                println("⬆️ PREPEND requested... scrolling up")

                val remoteKey = state.pages.firstOrNull { it.data.isNotEmpty() }
                    ?.data?.firstOrNull()?.let {
                        println("🔍 Checking remote key for first item: ${it.id}")
                        messageRemoteKeyDao.getRemoteKey(it.id)
                    }

                println("📦 PREPEND => remoteKey from DB = $remoteKey")

                val page = remoteKey?.prevKey
                if (page == null) {
                    println("🚫 PREPEND END -> No prevKey found. End of pagination.")
                    return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                }
                println("📄 PREPEND loading page = $page")
                page
            }
        }

        return try {

            println("\n🌍 Fetching messages from server...")
            println("➡️ roomId = $roomId")
            println("➡️ page = $page")
            println("➡️ pageSize = ${state.config.pageSize}")

            val response = messageService.getAllMessages(
                paginatedRequest = PaginatedRequest(
                    roomId = roomId,
                    page = page,
                    phoneNumbers = phoneNumbers
                )
            )

            if(response.isFailure){
                println("❌ Network failed: ${response.exceptionOrNull()?.message}")
                return MediatorResult.Error(response.exceptionOrNull()!!)
            }

            val paginatedResponse = response.getOrNull()
            println("📥 Server returned: ${paginatedResponse?.data?.size ?: 0} messages")

            val insertedIds = paginatedResponse?.data?.map { dto ->
                println("💾 Saving message -> ${dto.id} | ${dto.text ?: "NO TEXT"}")
                messageDao.upsert(messageToMessageEntity.map(dto))
            } ?: emptyList()

            if (insertedIds.isEmpty()) {
                println("⚠️ No messages inserted -> stopping pagination.")
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val endOfPagination = insertedIds.size < state.config.pageSize

            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (endOfPagination) null else page + 1

            println("🔑 Pagination Keys -> prev=$prevKey, next=$nextKey, end=$endOfPagination")

            val remoteKeys = paginatedResponse!!.data.map {
                MessageRemoteKey(
                    id = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey,
                    roomId = roomId
                )
            }

            println("💾 Storing ${remoteKeys.size} remote keys to DB...")
            messageRemoteKeyDao.insertAll(remoteKeys)

            println("🎉 SUCCESS -> Page Loaded | endOfPagination=$endOfPagination")
            MediatorResult.Success(endOfPagination)

        } catch (e: Exception) {
            println("❌ ERROR during load: ${e.message}")
            e.printStackTrace()
            MediatorResult.Error(e)
        }
    }
}
