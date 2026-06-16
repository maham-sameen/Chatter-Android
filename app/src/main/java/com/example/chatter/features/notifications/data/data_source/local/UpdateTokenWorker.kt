package com.example.chatter.features.notifications.data.data_source.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.chatter.features.notifications.domain.repository.NotificationRepo
import org.koin.core.component.KoinComponent

class UpdateTokenWorker (private val notificationRepo: NotificationRepo,
                         appContext: Context,
                         private val params: WorkerParameters
) : CoroutineWorker(appContext , params)  , KoinComponent {
    override suspend fun doWork(): Result {
        println("\uD83D\uDC68\u200D\uD83C\uDF93 [UpdateTokenWorker] doWork() started")
        val token = inputData.getString("token")
        if(token == null || token.isEmpty()){
            println("⚠️[UpdateTokenWorker] No data  found in input data")
            return Result.failure()
        }
        return try{
            notificationRepo.updateToken(token)
            println("\uD83D\uDC68\u200D\uD83C\uDF93 [UpdateTokenWorker] doWork() finish successfully")
            Result.success()
        }catch ( e: Exception){
            println("\uD83D\uDC68\u200D\uD83C\uDF93 [UpdateTokenWorker] doWork() error occured $e")
            Result.failure()
        }
    }
}