package com.example.chatter.core.data.data_source.mapper

import com.example.chatter.core.data.data_source.utils.Mapper
import com.example.chatter.core.domain.model.PhoneNumber
import com.example.chatter.core.data.data_source.model.remote.UserDto
import com.example.chatter.core.domain.model.User

class UserDtoToUser : Mapper<UserDto, User> {
    override fun map(from: UserDto): User {
        return User(
            username = from.username,
            phoneNumber = PhoneNumber(
                countryCode = from.phoneNumber.countryCode,
                phoneNumber = from.phoneNumber.phoneNumber
            )
        )
    }
}