package com.example.chatter.features.auth.domain.use_case

import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.auth.domain.model.SendCodeSignup
import com.example.chatter.features.auth.domain.repository.AuthRepo

class SendCodeForSignUp (
    private val authRepo: AuthRepo
){
    suspend  operator fun invoke(
         username : String,
         password : String,
         countryCode : String ,
         phoneNumber : String ,
         email : String
    ): Result<Unit>{
        println("\uD83E\uDD79 SendCodeForSignUp username : $username , password : $password , countryCode : $countryCode , phoneNumber : $phoneNumber")
        return authRepo.sendCodeForSignUp(
            SendCodeSignup(
                username = username,
                password = password,
                phoneNumber = PhoneNumber("+$countryCode", phoneNumber),
                email = email
            )
        )
    }
}