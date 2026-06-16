package com.example.chatter.features.auth.domain.use_case

import android.util.Patterns

class IsValidEmailAddress {
    operator fun invoke(email : String) : Boolean{
        return email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}