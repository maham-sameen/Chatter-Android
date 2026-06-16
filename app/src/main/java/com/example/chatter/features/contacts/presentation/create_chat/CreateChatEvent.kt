package com.example.chatter.features.contacts.presentation.create_chat

import androidx.compose.ui.focus.FocusState

sealed class CreateChatEvent {
    data class EnterName(val text : String) : CreateChatEvent()
    data class NameFocusChange(
        val focusState : FocusState
    ) : CreateChatEvent()
    data object Done  : CreateChatEvent()
    data class SelectImage(
        val image : ByteArray
    ): CreateChatEvent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SelectImage

            if (!image.contentEquals(other.image)) return false

            return true
        }

        override fun hashCode(): Int {
            return image.contentHashCode()
        }
    }
    object ClearUIState : CreateChatEvent()
}