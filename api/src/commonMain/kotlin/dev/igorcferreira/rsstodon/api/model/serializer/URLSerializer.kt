package dev.igorcferreira.rsstodon.api.model.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URL

object URLSerializer: KSerializer<URL> {
    override fun deserialize(decoder: Decoder): URL = URL(decoder.decodeString())
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("URL")
    override fun serialize(encoder: Encoder, value: URL) = encoder.encodeString(value.toString())
}