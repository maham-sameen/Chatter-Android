package com.example.chatter.features.auth.domain.use_case

import com.example.chatter.features.auth.domain.model.CheckCodeSignin
import com.example.chatter.features.auth.domain.repository.AuthRepo

class CheckCodeForSignIn(
    private val authRepo: AuthRepo
) {

    suspend operator fun invoke(
        username : String ,
        code : String
    ): Result<Unit>{
        println("\uD83E\uDD79 CheckCodeForSignIn username : $username , code : $code")
        return authRepo
            .checkCodeForSignIn(
                CheckCodeSignin(
                    code = code,
                    username = username
                )
            )
    }
}