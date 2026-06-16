package com.example.chatter.features.auth.data.data_source.model.mapper

import com.example.chatter.features.auth.domain.model.CheckCodeSignin
import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.features.auth.data.data_source.model.remote.CheckCodeSigninRequest

class CCSigninToCCSigninReq : Mapper<CheckCodeSignin , CheckCodeSigninRequest> {
    override fun map(from: CheckCodeSignin): CheckCodeSigninRequest {
        return CheckCodeSigninRequest(
            code = from.code,
            username = from.username
        )
    }
}