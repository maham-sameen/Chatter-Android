package com.example.chatter.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.InterceptPlatformTextInput
import kotlinx.coroutines.awaitCancellation

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DisableSoftKeyboard(content: @Composable () -> Unit) {
    InterceptPlatformTextInput(
        interceptor = { _, _ ->
            awaitCancellation()   // 👈 prevents the keyboard from opening
        },
        content = content
    )
}
