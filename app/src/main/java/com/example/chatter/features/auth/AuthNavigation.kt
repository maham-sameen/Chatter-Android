package com.example.chatter.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.chatter.features.auth.presentation.entercode.EnterCode
import com.example.chatter.features.auth.presentation.entercode.EnterCodeViewModel
import com.example.chatter.features.auth.presentation.intro.IntroScreen
import com.example.chatter.features.auth.presentation.signin.Signin
import com.example.chatter.features.auth.presentation.signin.SigninViewModel
import com.example.chatter.features.auth.presentation.signup.Signup
import com.example.chatter.features.auth.presentation.signup.SignupViewModel
import com.example.chatter.features.auth.presentation.utils.AuthScreen
import com.example.chatter.features.auth.presentation.utils.CustomNavType
import com.example.chatter.core.presentation.model.PhoneNumber
import com.example.chatter.core.presentation.utils.navigate
import com.example.chatter.core.presentation.utils.Graph
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.typeOf

fun NavGraphBuilder.authNavigation(
    navController : NavHostController ,
    goHome : () -> Unit
){
    navigation<Graph.Authentication>(
        startDestination = AuthScreen.Intro
    ){
        composable<AuthScreen.Intro> {
            IntroScreen(
                createAccount = {
                    navigate(
                        navController = navController,
                        route = AuthScreen.Signup
                    )
                },
                signIn = {
                    navigate(
                        navController = navController,
                        route = AuthScreen.Signin
                    )
                },

            )
        }
        composable<AuthScreen.Signin> {
            Signin(
                viewModel = koinViewModel<SigninViewModel>(),
                onSignIn = {
                    navigate(
                        navController = navController,
                        route = AuthScreen.EnterCode(it)
                    )
                }
            )
        }
        composable<AuthScreen.Signup> {
            val vm = koinViewModel<SignupViewModel>()
            Signup(
                viewModel = vm,
                onSignUp = {
                    println("\uD83E\uDD2D onSignUp")
                    navigate(
                        navController = navController,
                        route = AuthScreen.EnterCode(
                            phoneNumber = it
                        )
                    )
                }
            )
        }
        composable<AuthScreen.EnterCode>(
            typeMap = mapOf(typeOf<PhoneNumber?>() to CustomNavType(PhoneNumber.serializer()))
        ) {
            val phoneNumber  = it.toRoute<AuthScreen.EnterCode>().phoneNumber
            val username  = it.toRoute<AuthScreen.EnterCode>().username
            println("\uD83D\uDE0B EnterCode , phoneNumber : $phoneNumber , username : $username")
            val vm = koinViewModel<EnterCodeViewModel>()
            EnterCode(
                viewModel = vm,
                phoneNumber = phoneNumber,
                username = username,
                onSuccess = {home ->
                    if (home){
                        goHome()
                    }else{
                        navigate(
                            navController = navController,
                            route = AuthScreen.Intro
                        )
                    }
                },
                goHome = {
                    navigate(
                        navController = navController ,
                        route = AuthScreen.Intro
                    )
                }
            )
        }
    }
}

