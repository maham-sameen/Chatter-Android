package com.example.chatter.features.chats.data.repository

import androidx.work.WorkManager
import com.example.chatter.core.data.data_source.local.UserDataSource
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.local.LocalImageStorage
import com.example.chatter.features.chats.data.data_source.mapper.UserDbToUser
import com.example.chatter.features.chats.data.data_source.remote.RemoteImageStorage
import com.example.chatter.features.chats.domain.model.User
import com.example.chatter.features.chats.domain.repository.ProfileRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest



class ProfileRepoImp(
    private val userDataSource: UserDataSource,
    private val userDbToUser: UserDbToUser,
    private val localImageStorage: LocalImageStorage,
    private val remoteImageStorage: RemoteImageStorage
) : ProfileRepo {

    private val user = userDataSource.user

    override suspend fun updateUerProfile(image : ByteArray ,
                                          workManager: WorkManager): Result<Unit> {
        println("\uD83D\uDE1B ProfileRepoImp updateUserProfile")
        return runCatching {
            val filePath = localImageStorage.writeData(image)  ?: throw Exception("profile can not be updated")
            user.firstOrNull()?.let{
                uploadImageToWorker(
                    phoneNumber = it.phoneNumber,
                    workManager = workManager,
                    filePath = filePath
                )
            }
            userDataSource.updateUserProfile(filePath)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override  fun getUser(): Flow<User> {
       return user
           .filterNotNull()
           .mapLatest {
               println("\uD83D\uDE1B ProfileRepoImp getUser ${it.toString()}")
               userDbToUser.map(it)
       }
    }

    override suspend fun uploadImage(
        filePath: String ,
        phoneNumber: PhoneNumber) {
       remoteImageStorage.uploadImage(
           filePath = filePath,
           phoneNumber = phoneNumber
       )
    }

    override fun uploadImageToWorker(
        filePath : String ,
        phoneNumber: PhoneNumber , workManager: WorkManager) {
        remoteImageStorage.uploadImageToWorker(
            workManager = workManager,
            phoneNumber = phoneNumber,
            filePath = filePath
        )
    }
}