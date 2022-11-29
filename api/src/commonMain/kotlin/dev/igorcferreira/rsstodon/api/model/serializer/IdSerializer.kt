package dev.igorcferreira.rsstodon.api.model.serializer

import dev.igorcferreira.rsstodon.api.model.response.Id
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class IdSerializer: KSerializer<Id> {
    override fun deserialize(decoder: Decoder): Id = try {
        Id(decoder.decodeString())
    } catch (ex: Exception) {
        Id(decoder.decodeLong().toString())
    }
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Id")
    override fun serialize(encoder: Encoder, value: Id) = encoder.encodeString(value.value)
}