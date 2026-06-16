package com.example.chatter.core.domain.use_case

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.example.chatter.ui.ColorScheme

class FromHexCode {

    operator fun invoke(colorScheme: com.example.chatter.core.domain.model.ColorScheme) : ColorScheme{
        return ColorScheme(
            background =  Color(colorScheme.background.toColorInt()),
            icon = Color(colorScheme.icon.toColorInt())
        )
    }
}