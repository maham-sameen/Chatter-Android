
@file:OptIn(ExperimentalSerializationApi::class)

package com.example.chatter.core.data.data_source.utils

import androidx.datastore.core.Serializer
import com.example.chatter.core.data.data_source.model.local.UserPreference
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

internal class UserSerializer : Serializer<UserPreference?> {

    override val defaultValue: UserPreference? = null

    override suspend fun readFrom(input: InputStream): UserPreference? {
        val userInput = input.readBytes()
        if (userInput.isEmpty()) return null
        return ProtoBuf.decodeFromByteArray(UserPreference.serializer(), userInput)
    }

    override suspend fun writeTo(t: UserPreference?, output: OutputStream) {
        val userPreferences = t ?: return
        val byteArray = ProtoBuf.encodeToByteArray(
            UserPreference.serializer(),
            userPreferences,
        )

        @Suppress("BlockingMethodInNonBlockingContext")
        output.write(byteArray)
    }
}
