package com.example.chatter.features.chats.data.data_source.remote

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.chatter.core.data.data_source.local.LocalImageStorage
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.remote.GetDownloadUrl
import com.example.chatter.features.chats.data.data_source.local.ImageStorageWorker
import com.example.chatter.features.chats.data.data_source.model.remote.UpdateUserProfileRequest
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import java.util.concurrent.TimeUnit

class RemoteImageStorage(
    private val client: HttpClient,
    private val localImageStorage: LocalImageStorage,
    private val geetDownloadUrl: GetDownloadUrl
) {
    suspend fun uploadImage(
        filePath: String ,
        phoneNumber: PhoneNumber
    ) {
        println("😎 RemoteImageStorage: uploadImage ")
        try {
            val image = localImageStorage.readBytesFromPath(filePath) ?:return
            val downloadUrl = geetDownloadUrl.uploadImage(image)  ?:return
            client.post {
                url(UserService.EndPoints.UpdateUserProfile.url)
                setBody(
                    UpdateUserProfileRequest(
                        phoneNumber = phoneNumber,
                        image = downloadUrl
                    )
                )
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
        }
    }

    fun uploadImageToWorker(
        filePath : String,
        phoneNumber: PhoneNumber,
        workManager: WorkManager
    ) {
        println("📤 uploadImageToWorker called,")
        val dataGson = Gson().toJson(phoneNumber)

        val data = workDataOf(
            Pair("phone_number", dataGson),
            Pair("file_path", filePath)
        )

        val request = OneTimeWorkRequestBuilder<ImageStorageWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueue(request)
        println("🚀 WorkManager request enqueued!")
    }
}