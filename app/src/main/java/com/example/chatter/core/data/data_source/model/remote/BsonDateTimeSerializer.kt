package com.example.chatter.core.data.data_source.model.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.BsonDateTime

object BsonDateTimeSerializer : KSerializer<BsonDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BsonDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: BsonDateTime) {
        encoder.encodeLong(value.value) // store as epoch millis
    }

    override fun deserialize(decoder: Decoder): BsonDateTime {
        return BsonDateTime(decoder.decodeLong()) // restore back
    }
}