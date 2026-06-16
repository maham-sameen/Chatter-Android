package com.example.chatter.features.chats.domain.repository

import androidx.work.WorkManager
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.features.chats.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepo {

    suspend fun updateUerProfile(
        image : ByteArray ,
        workManager: WorkManager
    ) : Result<Unit>

    fun getUser() : Flow<User>

    suspend fun uploadImage(
        filePath: String ,
        phoneNumber: PhoneNumber)
    fun uploadImageToWorker(
        filePath : String ,
        phoneNumber: PhoneNumber,
        workManager : WorkManager
    )
}