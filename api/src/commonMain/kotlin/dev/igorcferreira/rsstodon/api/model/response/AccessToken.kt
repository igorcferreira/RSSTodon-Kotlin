package dev.igorcferreira.rsstodon.api.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    @SerialName("access_token") val token: String,
    @SerialName("token_type") val type: String,
    @SerialName("scope") val scope: String,
    @SerialName("created_at") val createdAt: Long
)