package com.example.chatter.features.auth.presentation.intro

sealed class IntroEvents {
    object Signup : IntroEvents()
    object Signin : IntroEvents()
}