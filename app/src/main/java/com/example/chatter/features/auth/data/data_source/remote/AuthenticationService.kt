package com.example.chatter.features.auth.data.data_source.remote

import com.example.chatter.Constants.BASE_URL

interface AuthenticationService {
    sealed class EndPoints(val url : String){
        object SignupSendCode : EndPoints("${BASE_URL}/signup/send_code")
        object SignupCheckCode : EndPoints("${BASE_URL}/signup/check_code")
        object SigninSendCode : EndPoints("${BASE_URL}/signin/send_code")
        object SigninCheckCode : EndPoints("${BASE_URL}/signin/check_code")
    }
}