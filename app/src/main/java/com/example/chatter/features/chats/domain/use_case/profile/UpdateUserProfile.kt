package com.example.chatter.features.chats.domain.use_case.profile


import androidx.work.WorkManager
import com.example.chatter.features.chats.domain.repository.ProfileRepo


class UpdateUserProfile(
    private val profileRepo: ProfileRepo
) {
    suspend operator fun invoke(image: ByteArray, workManager: WorkManager) : Result<Unit>{
        return profileRepo.updateUerProfile(image , workManager)
    }
}