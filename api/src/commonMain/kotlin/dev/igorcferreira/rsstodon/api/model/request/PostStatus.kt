package dev.igorcferreira.rsstodon.api.model.request

import dev.igorcferreira.rsstodon.api.model.response.Visibility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PostStatus(
    @SerialName("status") val message: String,
    val language: String?,
    val visibility: Visibility?
)
