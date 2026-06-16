package com.example.chatter.features.auth.di

import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.features.auth.domain.repository.AuthRepo
import com.example.chatter.features.auth.domain.use_case.AuthUseCases
import com.example.chatter.features.auth.domain.use_case.CheckCodeForSignIn
import com.example.chatter.features.auth.domain.use_case.CheckCodeForSignUp
import com.example.chatter.features.auth.domain.use_case.GetAllUsers
import com.example.chatter.features.auth.domain.use_case.SendCodeForSignIn
import com.example.chatter.features.auth.domain.use_case.SendCodeForSignUp
import com.example.chatter.features.auth.presentation.entercode.EnterCodeViewModel
import com.example.chatter.features.auth.presentation.signin.SigninViewModel
import com.example.chatter.features.auth.presentation.signup.SignupViewModel
import com.example.chatter.core.domain.use_case.IsValidPhoneNo
import com.example.chatter.core.domain.use_case.ToHexCode
import com.example.chatter.features.auth.domain.use_case.IsValidEmailAddress
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope

@OptIn(KoinExperimentalAPI::class)
val  authViewModelModule  = module{
    viewModelOf(::SignupViewModel)
    viewModelOf(::SigninViewModel)
    viewModelOf(::EnterCodeViewModel)
    viewModelScope {
        scoped {
            val authRepo by inject<AuthRepo>()
            AuthUseCases(
                isValidPhoneNo = get<IsValidPhoneNo>(),
                sendCodeForSignUp = SendCodeForSignUp(authRepo),
                checkCodeForSignUp = CheckCodeForSignUp(
                    authRepo,
                    toHexCode = get<ToHexCode>(),
                    getRandomColor = get<GetRandomColor>()
                ),
                sendCodeForSignIn = SendCodeForSignIn(authRepo),
                checkCodeForSignIn = CheckCodeForSignIn(authRepo),
                getAllUsers = GetAllUsers(authRepo),
                isValidEmailAddress = IsValidEmailAddress(),
            )
        }
    }
}