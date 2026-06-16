package com.example.chatter.features.auth.presentation.utils

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.let

class CustomNavType<T>(private val serializer: KSerializer<T>) :
    NavType<T?>(isNullableAllowed = true) {

    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let { Json.Default.decodeFromString(serializer, it) }

    override fun parseValue(value: String): T? =
        Json.Default.decodeFromString(serializer, Uri.decode(value))

    override fun serializeAsValue(value: T?): String =
        value?.let { Uri.encode(Json.Default.encodeToString(serializer, it)) } ?: "null"

    override fun put(bundle: Bundle, key: String, value: T?) {
        if (value == null) {
            bundle.putString(key, null)
        } else {
            bundle.putString(key, Json.Default.encodeToString(serializer, value))
        }
    }
}
