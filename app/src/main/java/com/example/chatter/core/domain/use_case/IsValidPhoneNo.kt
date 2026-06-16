package com.example.chatter.core.domain.use_case

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

class IsValidPhoneNo {
    operator fun invoke(phone: String, regionCode: String = "US") : Boolean{
        println("\uD83D\uDE08 ContactsUseCase , IsValidPhoneNo phone $phone regionCode $regionCode")
        return try {
            val phoneUtil = PhoneNumberUtil.getInstance()
            val number = phoneUtil.parse(phone, regionCode)
            phoneUtil.isValidNumber(number)
        } catch (e: NumberParseException) {
            println("\uD83D\uDE08 ContactsUseCase , UsValidPhoneNo ${e.message}")
            false
        }
    }
}