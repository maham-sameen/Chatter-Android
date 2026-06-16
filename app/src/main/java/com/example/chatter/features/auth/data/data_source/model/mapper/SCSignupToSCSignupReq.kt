package com.example.chatter.features.auth.data.data_source.model.mapper

import com.example.chatter.features.auth.data.data_source.model.remote.SendCodeSignupRequest
import com.example.chatter.features.auth.domain.model.SendCodeSignup
import com.example.chatter.core.data.data_source.model.local.PhoneNumber
import com.example.chatter.core.data.data_source.utils.Mapper

class SCSignupToSCSignupReq : Mapper<SendCodeSignup , SendCodeSignupRequest> {
    override fun map(from: SendCodeSignup): SendCodeSignupRequest {
        return SendCodeSignupRequest(
            username = from.username,
            password = from.password,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            ),
            email = from.email
        )
    }
}