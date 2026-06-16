package com.example.chatter.features.notifications.di


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chatter.Constants
import com.example.chatter.Constants.MAIN_CHANNEL
import com.example.chatter.Constants.MAIN_CHANNEL_ID
import com.example.chatter.R
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.features.calls.data.data_source.remote.CallDevice
import com.example.chatter.features.notifications.data.data_source.local.UpdateTokenWorker
import com.example.chatter.features.notifications.data.data_source.remote.UpdateTokenService
import com.example.chatter.features.notifications.data.repository.NotificationRepoImp
import com.example.chatter.features.notifications.domain.repository.NotificationRepo
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module


val notificationAppModule = module {
    workerOf(::UpdateTokenWorker)
    single<NotificationCompat.Builder> {
        NotificationCompat.Builder(
             androidApplication(),
            MAIN_CHANNEL_ID
        ).setContentTitle("Meeting has Scheduled")
            .setSmallIcon(R.drawable.darklogo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setPublicVersion(
                NotificationCompat.Builder(
                    androidApplication() ,
                    MAIN_CHANNEL_ID
                ).setContentTitle("Hidden")
                    .setContentText("Unlock to see message")
                    .build()
            )
    }

    single<NotificationManagerCompat>{
        val notificationManager = NotificationManagerCompat.from(androidApplication())
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                MAIN_CHANNEL_ID ,
                MAIN_CHANNEL  ,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
         notificationManager
    }

    single {
        UpdateTokenService(
            client = get<HttpClient>(),
            userDataSource = get<UserDataSource>(),
            callDevice = get<CallDevice>(
            )
        )
    }

    single<NotificationRepo>{
        NotificationRepoImp(
            updateTokenService = get<UpdateTokenService>()
        )
    }
}