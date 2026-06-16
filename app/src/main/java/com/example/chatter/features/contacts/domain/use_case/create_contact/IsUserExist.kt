package com.example.chatter.features.contacts.domain.use_case.create_contact

import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.core.domain.model.User
import com.example.chatter.features.contacts.domain.repository.ContactRepo

class IsUserExist(
    private val contactRepo: ContactRepo
) {

    suspend operator fun invoke(
        name : String ,
        phoneNumber: com.example.chatter.core.presentation.model.PhoneNumber
    ): Result<Boolean>{
        return contactRepo.getAllUsers().fold(
            onSuccess = {
                println("\uD83C\uDF38 GetAllUsersUseCase , Success : ${it.toString()}")
                val user = User(
                    username = name,
                    phoneNumber = PhoneNumber(
                        countryCode = phoneNumber.countryCode ,
                        phoneNumber = phoneNumber.phoneNumber
                    )
                )
                val currentUser = contactRepo.getCurrentUserId()
                if(user.phoneNumber.toString() == currentUser){
                    return Result.failure(Exception("can not make self contact"))
                }

                Result.success(
                    it.any { item -> item == user }
                )
            },
            onFailure = {
                println("\uD83C\uDF38 GetAllUsersUseCase , failure : ${it.toString()}")
                Result.failure(Exception("Something went wrong"))
            }
        )
    }
}