package com.example.chatter.features.contacts.domain.use_case.create_contact

import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.features.contacts.domain.model.Contact
import com.example.chatter.features.contacts.domain.repository.ContactRepo

class InsertContact (
    private val contactRepo: ContactRepo,
    private val isUserExist: IsUserExist
){

    /**
     * here we are getting the phone number in this formate
     *  ( countryCode : +92 , phoneNumber : 3276010123 )
     */
    suspend operator fun invoke(
        name : String,
        phoneNumber : PhoneNumber,
    ) : Result<Unit>{
        isUserExist(
            name = name,
            phoneNumber = phoneNumber
        ).fold(
            onSuccess = {
                if(!it){
                    return Result.failure(Exception("User has no account on chatter app"))
                }
            },
            onFailure = {
                return Result.failure(it)
            }
        )
        val pNo = com.example.chatter.core.domain.model.PhoneNumber(
            countryCode = phoneNumber.countryCode,
            phoneNumber = phoneNumber.phoneNumber
        )

        /**
         * userId -> 92-3276010123
         * isUser is also use this formate for comparing
         */
        val userId = contactRepo.getCurrentUserId()
        val isUser = phoneNumber.toString() == userId
        println("\uD83D\uDE08 ContactsUseCase : InsertContact  , userId : $userId , isUser : $isUser")
        val contact = Contact(
            userId = userId,
            name = name,
            phoneNumber = pNo,
            image = null,
            colorScheme = null,
            isUser = isUser
        )
        return contactRepo.getFullContact(contact).fold(
            onSuccess = {
                println("\uD83D\uDE08 ContactsUseCase : InsertContact  onSuccess : ${it.toString()}")
                val result = contactRepo.insertContact(it)
                if(result){
                    Result.success(Unit)
                }else{
                    Result.failure(Exception("Something went wrong"))
                }
            },
            onFailure = {
                println("\uD83D\uDE08 ContactsUseCase : InsertContact  onFailure : ${it.toString()}")
                Result.failure(Exception("Something went wrong"))
            }
        )
    }

}