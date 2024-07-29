package de.blazemcworld.fireflow.database

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data class SimpleStrSerialization<T>(val to: (T) -> String, val from: (String) -> T) : KSerializer<T> {
    override val descriptor = PrimitiveSerialDescriptor("SimpleStrSerialization", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = from(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(to(value))
}

data class SimpleIntSerialization<T>(val to: (T) -> Int, val from: (Int) -> T) : KSerializer<T> {
    override val descriptor = PrimitiveSerialDescriptor("SimpleIntSerialization", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder) = from(decoder.decodeInt())
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeInt(to(value))
}