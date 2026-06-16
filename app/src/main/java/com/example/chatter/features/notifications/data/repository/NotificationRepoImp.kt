package com.example.chatter.features.notifications.data.repository

import androidx.work.WorkManager
import com.example.chatter.features.notifications.data.data_source.remote.UpdateTokenService
import com.example.chatter.features.notifications.domain.repository.NotificationRepo

class NotificationRepoImp(
    private val updateTokenService: UpdateTokenService
) : NotificationRepo {
    override fun updateTokenToWorker(token: String, workManager: WorkManager) {
        updateTokenService
            .uploadDateToWorker(
                token = token,
                workManager = workManager
            )
    }

    override suspend fun updateToken(token: String) {
        updateTokenService
            .updateToken(token = token)
    }
}