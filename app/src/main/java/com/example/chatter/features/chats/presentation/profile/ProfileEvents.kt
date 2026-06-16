package com.example.chatter.features.chats.presentation.profile


sealed class ProfileEvents {
    data class SelectImage(
        val image : ByteArray
    ): ProfileEvents() {
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

    object Logout : ProfileEvents()
    object ClearUIState: ProfileEvents()
}