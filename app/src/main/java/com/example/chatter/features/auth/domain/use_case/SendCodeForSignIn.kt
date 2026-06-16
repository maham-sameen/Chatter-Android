package com.example.chatter.features.auth.domain.use_case

import com.example.chatter.features.auth.domain.model.SendCodeSignin
import com.example.chatter.features.auth.domain.repository.AuthRepo

class SendCodeForSignIn (
    private val authRepo: AuthRepo
){
    suspend  operator fun invoke(
         username : String,
         password : String,
    ): Result<Unit>{
        println("\uD83E\uDD79 SendCodeForSignIn username : $username , password : $password ")
        return authRepo.sendCodeForSignIn(
            SendCodeSignin(
                username = username,
                password = password
            )
        )
    }
}