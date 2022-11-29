package dev.igorcferreira.rsstodon.api.model.response

import dev.igorcferreira.rsstodon.api.model.serializer.URLSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.net.URL

@Serializable
data class Attachment(
    val id: Id,
    val type: Type,
    @Serializable(with = URLSerializer::class)
    val url: URL,
    @Serializable(with = URLSerializer::class)
    @SerialName("preview_url") val previewURL: URL?,
    val description: String?,
    @Serializable(with = URLSerializer::class)
    @SerialName("remote_url") val remoteURL: URL?,
    val meta: Map<String, JsonElement>?,
    @SerialName("blurhash") val blurHash: String?
) {
    @Serializable
    enum class Type {
        @SerialName("unknown")
        UNKNOWN,
        @SerialName("image")
        IMAGE,
        @SerialName("gifv")
        GIFV,
        @SerialName("video")
        VIDEO,
        @SerialName("audio")
        AUDIO
    }
}
