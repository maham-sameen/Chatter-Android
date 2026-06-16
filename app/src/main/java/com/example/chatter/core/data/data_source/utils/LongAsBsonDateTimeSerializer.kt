package com.example.chatter.core.data.data_source.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.BsonDateTime

object LongAsBsonDateTimeSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LongAsBsonDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Long) {
        // Convert Long → BsonDateTime before sending
        val bsonTime = BsonDateTime(value)
        encoder.encodeLong(bsonTime.value) // backend expects millis
    }

    override fun deserialize(decoder: Decoder): Long {
        // Convert BsonDateTime(Long) → Long when receiving
        val millis = decoder.decodeLong()
        return BsonDateTime(millis).value
    }
}
