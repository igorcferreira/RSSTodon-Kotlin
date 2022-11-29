package dev.igorcferreira.rsstodon.api.model.response

import dev.igorcferreira.rsstodon.api.model.serializer.ISODateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ScheduledStatus(
    val id: Id,
    @Serializable(with = ISODateSerializer::class)
    @SerialName("scheduled_at") val scheduledAt: Date,
    val params: Params,
    val attachments: List<Attachment>?
) {
    @Serializable
    data class Params(
        val text: String,
        val visibility: Visibility?,
        @SerialName("application_id") val applicationId: Id?,
        @SerialName("in_reply_to_id") val replyTo: String?,
        val sensitive: Boolean?,
        @SerialName("spoiler_text") val spoilerText: String?,
        @SerialName("media_ids") val medias: List<Id>?
    )
}
