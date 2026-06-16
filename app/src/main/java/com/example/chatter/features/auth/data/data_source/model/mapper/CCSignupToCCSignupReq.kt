package com.example.chatter.features.auth.data.data_source.model.mapper

import com.example.chatter.core.data.data_source.model.local.ColorScheme
import com.example.chatter.features.auth.domain.model.CheckCodeSignup
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.auth.data.data_source.model.remote.CheckCodeSignupRequest

class CCSignupToCCSignupReq : Mapper<CheckCodeSignup , CheckCodeSignupRequest> {
    override fun map(from: CheckCodeSignup): CheckCodeSignupRequest {
        return CheckCodeSignupRequest(
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            code = from.code,
            colorScheme = ColorScheme(
                background = from.colorScheme.background,
                icon = from.colorScheme.icon
            )
        )
    }
}