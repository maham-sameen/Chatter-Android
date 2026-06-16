package com.example.chatter.features.auth.domain.use_case

import com.example.chatter.core.domain.model.User
import com.example.chatter.features.auth.domain.repository.AuthRepo

class GetAllUsers(
    private val authRepo: AuthRepo
) {

    suspend operator fun invoke(): List<User>{
        println("\uD83E\uDD79 GetAllUsers")
        return  authRepo.getAllUsers()
            .fold(
                onSuccess = {
                    println("\uD83E\uDD79 GetAllUsers onSuccess")
                    it
                },
                onFailure = {
                    println("\uD83E\uDD79 GetAllUsers onFailure")
                    listOf()
                }
            )
    }
}