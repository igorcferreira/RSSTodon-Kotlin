package dev.igorcferreira.rsstodon.api.model.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

object ISODateSerializer: KSerializer<Date> {
    private const val ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    override fun deserialize(decoder: Decoder): Date = SimpleDateFormat(ISO_8601_24H_FULL_FORMAT)
        .parse(decoder.decodeString())
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Date")
    override fun serialize(encoder: Encoder, value: Date) = encoder
        .encodeString(SimpleDateFormat(ISO_8601_24H_FULL_FORMAT).format(value))
}