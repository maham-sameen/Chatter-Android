package com.example.chatter.features.chats.data.data_source.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.chats.domain.repository.ProfileRepo
import com.google.gson.Gson
import org.koin.core.component.KoinComponent

class ImageStorageWorker(
    private val profileRepo: ProfileRepo,
    appContext: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContext , params)  , KoinComponent {
    override suspend fun doWork(): Result {
        println("😃 ImageStorageWorker doWork() started")

        val text = inputData.getString("phone_number")
        val filePath : String? = inputData.getString("file_path")
        if (text == null || filePath == null ) {
            println("⚠️ No data  found in input data")
            return Result.failure()
        }
        return try {
            val phoneNumber  = Gson().fromJson(text , PhoneNumber::class.java)
            println("📸 filePath received: $filePath")
            println("😃 ImageStorageWorker doWork() phoneNumber : $phoneNumber")
            profileRepo.uploadImage(
                filePath = filePath,
                phoneNumber = phoneNumber
            ) // <-- Example function in your repo

            println("✅ Image processed successfully")
            Result.success()

        } catch (e: Exception) {
            println("❌ Exception in doWork(): ${e.message}")
            Result.failure()
        }
    }
}