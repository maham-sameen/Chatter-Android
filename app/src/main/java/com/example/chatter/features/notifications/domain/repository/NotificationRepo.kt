package com.example.chatter.features.notifications.domain.repository

import androidx.work.WorkManager

interface NotificationRepo {

    fun updateTokenToWorker(
        token : String ,
        workManager: WorkManager
    )

    suspend fun updateToken(
        token : String
    )
}