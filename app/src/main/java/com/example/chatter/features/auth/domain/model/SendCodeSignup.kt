package com.example.chatter.features.auth.domain.model

import com.example.chatter.core.domain.model.PhoneNumber


data class SendCodeSignup(val username : String,
                          val password : String,
                          val phoneNumber : PhoneNumber,
                          val email : String)
