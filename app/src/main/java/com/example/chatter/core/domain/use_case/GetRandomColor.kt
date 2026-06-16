package com.example.chatter.core.domain.use_case

import com.example.chatter.ui.ColorScheme
import com.example.chatter.ui.theme.colorSchemes

class GetRandomColor {

    operator fun invoke(): ColorScheme {
        println("\uD83D\uDE08 ContactsUseCase : GetRandomColor")
        return colorSchemes.random()
    }
}