package com.example.chatter.features.chats.data.data_source.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.chatter.features.chats.domain.repository.ChatRepo
import com.google.gson.Gson
import org.koin.core.component.KoinComponent

class UpdateUnreadMessagesWorker (
    private val chatRepo : ChatRepo ,
    appContext: Context,
    private val params: WorkerParameters) : CoroutineWorker(appContext , params)  , KoinComponent {
    override suspend fun doWork(): Result {
        println("\uD83D\uDED2 [UpdateUnreadMessagesWorker] doWork() started")
        val roomId = inputData.getString("room_id")
        val timeStampJson = inputData.getString("time_stamp")
        val lastMessage = inputData.getString("last_message")
        if(roomId == null ||
            roomId.isEmpty() ||
            timeStampJson == null ||
            timeStampJson.isEmpty() ||
            lastMessage == null || lastMessage.isEmpty()){
            println("\uD83D\uDED2 [UpdateUnreadMessagesWorker] any input data is null")
            return Result.failure()
        }
        return try {
            val timeStamp  = Gson().fromJson(timeStampJson , Long::class.java)
            chatRepo.updateUnreadMessages(
                roomId = roomId,
                timeStamp = timeStamp,
                lastMessage = lastMessage
            )
            println("✅ [UpdateUnreadMessagesWorker] Unread Messages updated successfully")
            Result.success()

        } catch (e: Exception) {
            println("❌[UpdateUnreadMessagesWorker]  Exception in doWork(): ${e.message}")
            Result.failure()
        }
    }
}