package com.example.chatter.features.auth.domain.use_case

import com.example.chatter.core.domain.use_case.IsValidPhoneNo

data class AuthUseCases(
    val isValidPhoneNo: IsValidPhoneNo ,
    val sendCodeForSignUp: SendCodeForSignUp ,
    val checkCodeForSignUp: CheckCodeForSignUp ,
    val sendCodeForSignIn: SendCodeForSignIn ,
    val checkCodeForSignIn: CheckCodeForSignIn ,
    val getAllUsers: GetAllUsers ,
    val isValidEmailAddress: IsValidEmailAddress
)
