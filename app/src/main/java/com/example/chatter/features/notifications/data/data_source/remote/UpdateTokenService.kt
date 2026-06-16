package com.example.chatter.features.notifications.data.data_source.remote

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.calls.data.data_source.remote.CallDevice
import com.example.chatter.features.chats.data.data_source.local.UpdateUnreadMessagesWorker
import com.example.chatter.features.notifications.data.data_source.model.remote.FcmTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class UpdateTokenService(
    private val client : HttpClient,
    private val userDataSource: UserDataSource,
    private val callDevice : CallDevice
) {

    suspend fun updateToken(
        token : String
    ){
        println("\uD83E\uDD8A [FcmTokenService] updateFcmToken , token : $token ")
        userDataSource.user.firstOrNull()?.let {
            updateTokenToServer(
                phoneNumber = it.phoneNumber,
                token = token
            )
            callDevice.createDevice(token)
        }
    }




    private suspend fun updateTokenToServer(
        phoneNumber : PhoneNumber,
        token : String,
    ) : Result<Unit> {
        println("\uD83C\uDFAA [FcmTokenService] updateFcmTokenToServer  ,  phoneNumber  : $phoneNumber , token : $token")
        return runCatching {
            val response = client.post {
                url(TokenService.EndPoints.UpdateToken.url)
                setBody(
                    FcmTokenRequest(
                        phoneNumber = phoneNumber,
                        fcmToken = token
                    )
                )
                contentType(ContentType.Application.Json)
            }
            val body = response.body<String>()
            println("\uD83D\uDE1B [FcmTokenService] updateFcmTokenToServer : body : $body")
            when (response.status) {
                HttpStatusCode.Companion.OK -> {
                    val isSuccess = Json.Default.decodeFromString<Boolean>(body)
                    if (isSuccess) {
                        println("\uD83C\uDFB2 [FcmTokenService] updateFcmTokenToServer isSuccess ")
                        Result.success(Unit)
                    } else {
                        println("\uD83C\uDFB2 [FcmTokenService] updateFcmTokenToServer isFailure ")
                        Result.failure(Exception("Notification can `t be activated due to some errors"))
                    }
                }

                HttpStatusCode.Companion.BadRequest -> throw Exception("Bad request")
                else -> throw Exception("Unexpected error: $body")
            }
        }
    }

    fun uploadDateToWorker(
        token : String ,
        workManager: WorkManager
    ) {
        println("\uD83C\uDF3F [FcmTokenService] uploadDataToWorker , token : $token")

        val data = workDataOf(
            Pair("token", token),
        )

        val request = OneTimeWorkRequestBuilder<UpdateUnreadMessagesWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueue(request)
        println("🚀[FcmTokenService] WorkManager request enqueued!")
    }
}