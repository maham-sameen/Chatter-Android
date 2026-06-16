package com.example.chatter.features.auth.data.data_source.model.mapper

import com.example.chatter.features.auth.data.data_source.model.remote.SendCodeSigninRequest
import com.example.chatter.features.auth.domain.model.SendCodeSignin
import com.example.chatter.core.data.data_source.utils.Mapper

class SCSigninToSCSigninReq : Mapper<SendCodeSignin , SendCodeSigninRequest> {
    override fun map(from: SendCodeSignin): SendCodeSigninRequest {
        return SendCodeSigninRequest(
            username = from.username,
            password = from.password
        )
    }
}