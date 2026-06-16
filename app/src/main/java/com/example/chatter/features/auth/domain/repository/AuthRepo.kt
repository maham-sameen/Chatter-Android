package com.example.chatter.features.auth.domain.repository

import com.example.chatter.features.auth.domain.model.CheckCodeSignin
import com.example.chatter.features.auth.domain.model.CheckCodeSignup
import com.example.chatter.features.auth.domain.model.SendCodeSignin
import com.example.chatter.features.auth.domain.model.SendCodeSignup
import com.example.chatter.core.domain.model.User

interface AuthRepo {

    suspend fun sendCodeForSignUp(
        sendCodeSignUp: SendCodeSignup
    ): Result<Unit>

    suspend fun sendCodeForSignIn(
        sendCodeSignIn: SendCodeSignin
    ) : Result<Unit>

    suspend fun checkCodeForSignUp(
        checkCodeSignUp: CheckCodeSignup
    ): Result<Unit>

    suspend fun checkCodeForSignIn(
        checkCodeSignIn : CheckCodeSignin
    ): Result<Unit>

    suspend fun getAllUsers() : Result<List<User>>
}