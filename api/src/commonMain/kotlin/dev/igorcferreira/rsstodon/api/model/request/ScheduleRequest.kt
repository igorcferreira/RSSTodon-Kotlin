package dev.igorcferreira.rsstodon.api.model.request

import dev.igorcferreira.rsstodon.api.model.response.Visibility
import dev.igorcferreira.rsstodon.api.model.serializer.ISODateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ScheduleRequest(
    @SerialName("status") val message: String,
    @Serializable(with = ISODateSerializer::class)
    @SerialName("scheduled_at") val date: Date,
    val language: String?,
    val visibility: Visibility?
)
