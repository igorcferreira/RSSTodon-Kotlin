package dev.igorcferreira.rsstodon.api.model.presentation

data class StatusContent(
    val id: String,
    val account: String,
    val uri: String,
    val content: String
)