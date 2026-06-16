package com.example.chatter.features.auth.data.data_source.model.remote

import com.example.chatter.core.data.data_source.model.local.User
import com.example.chatter.core.data.data_source.model.local.UserPreference
import com.example.chatter.core.data.data_source.utils.Mapper

class AuthResponseToUserPreference : Mapper<AuthResponse , UserPreference> {
    override fun map(from: AuthResponse): UserPreference {
        return UserPreference(
            user = User(
                name = from.name,
                phoneNumber = from.phoneNumber,
                colorScheme = from.colorScheme,
                image = from.image
            ),
            userToken = from.token
        )
    }
}