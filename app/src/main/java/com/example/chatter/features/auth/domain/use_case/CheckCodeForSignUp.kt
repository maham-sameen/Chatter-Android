package com.example.chatter.features.auth.domain.use_case

import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.core.domain.use_case.ToHexCode
import com.example.chatter.features.auth.domain.model.CheckCodeSignup
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.features.auth.domain.repository.AuthRepo



class CheckCodeForSignUp(
    private val authRepo: AuthRepo  ,
    private val toHexCode: ToHexCode ,
    private val getRandomColor: GetRandomColor
) {

    suspend operator fun invoke(
        countryCode : String ,
        phoneNumber : String ,
        code : String
    ): Result<Unit>{
        println("\uD83E\uDD79 CheckCodeForSignUp countryCode : $countryCode , phoneNumber : $phoneNumber , code : $code")
        return authRepo
            .checkCodeForSignUp(
                checkCodeSignUp = CheckCodeSignup(
                    phoneNumber = PhoneNumber(
                        countryCode = "+${countryCode}",
                        phoneNumber = phoneNumber
                    ),
                    code = code,
                    colorScheme = toHexCode(getRandomColor())
                )
            )
    }
}