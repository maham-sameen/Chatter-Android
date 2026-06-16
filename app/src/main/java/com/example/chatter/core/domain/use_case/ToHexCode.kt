package com.example.chatter.core.domain.use_case

import androidx.compose.ui.graphics.Color
import com.example.chatter.ui.ColorScheme

class ToHexCode {

    operator fun invoke(colorScheme: ColorScheme) : com.example.chatter.core.domain.model.ColorScheme {
        return com.example.chatter.core.domain.model.ColorScheme(
            background = toHexCode(colorScheme.background),
            icon = toHexCode(colorScheme.icon)
        )
    }

    private fun toHexCode(color  : Color) : String{
        return String.format(
            "#%08X",
            (color.alpha * 255).toInt() shl 24 or
                    (color.red * 255).toInt() shl 16 or
                    (color.green * 255).toInt() shl 8 or
                    (color.blue * 255).toInt()
        )
    }
}