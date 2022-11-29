package dev.igorcferreira.rsstodon.api.model.response

import dev.igorcferreira.rsstodon.api.model.serializer.ISODateSerializer
import dev.igorcferreira.rsstodon.api.model.serializer.URLSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.net.URL
import java.util.*

@Serializable
data class Status(
    val id: Id,
    @Serializable(with = URLSerializer::class)
    val uri: URL,
    @Serializable(with = URLSerializer::class)
    val url: URL?,
    @Serializable(with = ISODateSerializer::class)
    @SerialName("created_at") val createdAt: Date,
    val content: String,
    val visibility: Visibility,
    val sensitive: Boolean,
    @SerialName("spoiler_text") val spoilerText: String,
    @SerialName("media_attachments") val attachments: List<Attachment>,
    val account: JsonObject,
    val application: JsonElement?,
    val mentions: List<JsonElement>,
    val tags: List<JsonElement>,
    val emojis: List<JsonElement>,
    @SerialName("reblogs_count") val reblogsCount: Int,
    @SerialName("favourites_count") val favouritesCount: Int,
    @SerialName("replies_count") val repliesCount: Int,
    @SerialName("in_reply_to_id") val replyToPost: Id?,
    @SerialName("in_reply_to_account_id") val replyToAccount: Id?,
    val reblog: Status?,
    val poll: JsonElement?,
    val card: JsonElement?,
    val language: String?,
    val text: String?,
    val favourited: Boolean?,
    val reblogged: Boolean?,
    val muted: Boolean?,
    val bookmarked: Boolean?,
    val pinned: Boolean?
) {
    override fun equals(other: Any?): Boolean {
        val otherStatus = other as? Status ?: return super.equals(other)
        return id == otherStatus.id
    }

    override fun hashCode(): Int = id.hashCode()
}
