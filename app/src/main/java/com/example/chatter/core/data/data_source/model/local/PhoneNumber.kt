package com.example.chatter.core.data.data_source.model.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneNumber(
    // +92
    @SerialName("country_code")val countryCode : String,
    // 3276010123
    @SerialName("phone_number")val phoneNumber : String
){
    /**
     * we are removing that prefix because when we make call
     * like phonenumber( country code = +92 , phoneNumber = 1234324)  in request body we get whole but in
     * web socket when we pass then that + sign is not present
     *
     *
     * PhoneNumber(+92 , 3276010123) -> 92-32761010123
     */
    override fun toString(): String {
        val cleanCode = countryCode.removePrefix("+")
        return "$cleanCode-$phoneNumber"
    }
    companion object {
        /**
         *  92-32761010123 -> PhoneNumber(+92 , 3276010123) -> 92-32761010123
         */
        fun fromString(raw: String): PhoneNumber {
            val parts = raw.split("-")
            val code = parts[0]          // add back the "+"
            val number = parts[1]
            return PhoneNumber("+$code", number)
        }
    }
}