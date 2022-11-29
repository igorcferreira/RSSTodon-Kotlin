package dev.igorcferreira.rsstodon.api.domain

import dev.igorcferreira.rsstodon.api.model.response.AccessToken
import dev.igorcferreira.rsstodon.api.model.response.ScheduledStatus
import dev.igorcferreira.rsstodon.api.model.response.Status

internal sealed class Endpoint<Response>(
    val url: String,
    val headers: Map<String, String> = emptyMap()
) {
    internal object PublicTimeline: Endpoint<List<Status>>("/api/v1/timelines/public")
    internal object HomeTimeline: Endpoint<List<Status>>("/api/v1/timelines/home")
    internal object GetAuthenticationToken: Endpoint<AccessToken>("/oauth/token")
    internal class ScheduleStatus(key: String?): Endpoint<ScheduledStatus>(
        "/api/v1/statuses",
        key?.let { mapOf("Idempotency-Key" to key) } ?: emptyMap()
    )
    internal class StatusEndpoint(key: String?): Endpoint<Status>(
        "/api/v1/statuses",
        key?.let { mapOf("Idempotency-Key" to key) } ?: emptyMap()
    )
    internal class GetStatus(id: String): Endpoint<Status>("/api/v1/statuses/$id")
}
