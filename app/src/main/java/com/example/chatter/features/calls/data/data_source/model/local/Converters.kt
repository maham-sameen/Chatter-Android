package com.example.chatter.features.calls.data.data_source.model.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStatus(value: CallStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): CallStatus = CallStatus.valueOf(value)
}