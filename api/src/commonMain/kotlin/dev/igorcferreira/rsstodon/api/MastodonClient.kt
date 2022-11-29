package dev.igorcferreira.rsstodon.api

import dev.igorcferreira.rsstodon.api.domain.Endpoint
import dev.igorcferreira.rsstodon.api.domain.NetworkDataSource
import dev.igorcferreira.rsstodon.api.model.Configuration
import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.api.model.request.PostStatus
import dev.igorcferreira.rsstodon.api.model.request.ScheduleRequest
import dev.igorcferreira.rsstodon.api.model.response.ScheduledStatus
import dev.igorcferreira.rsstodon.api.model.response.Status
import dev.igorcferreira.rsstodon.api.model.response.Visibility
import dev.igorcferreira.rsstodon.api.repository.InMemoryStatusRepository
import dev.igorcferreira.rsstodon.api.repository.StatusRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.net.URI
import java.util.*

object MissingTokenException: Exception("Not Authorized")

@Suppress("unused")
open class MastodonClient internal constructor(
    private val configuration: Configuration,
    private val statusRepository: StatusRepository,
    private val dataSource: NetworkDataSource
) {
    constructor(
        configuration: Configuration
    ): this(configuration, NetworkDataSource(configuration))

    constructor(
        configuration: Configuration,
        statusRepository: StatusRepository
    ): this(configuration, statusRepository, NetworkDataSource(configuration))

    internal constructor(
        configuration: Configuration,
        dataSource: NetworkDataSource
    ): this(configuration, InMemoryStatusRepository(), dataSource)

    open val isAuthenticated: Boolean
        get() = configuration.authentication?.tokenStorage?.token.isNullOrEmpty().not()

    open val authorizationURI: URI?
        get() = configuration.authorizationUri

    open suspend fun authenticate(code: String) = dataSource.authenticate(code)

    open fun getTimelineFlow(): Flow<List<StatusContent>> =
        statusRepository.statusList()
    open suspend fun refreshTimeline(public: Boolean = true) {
        dataSource.get(
            if (public) Endpoint.PublicTimeline else Endpoint.HomeTimeline
        ).let {
            statusRepository.add(it)
        }
    }

    open suspend fun status(id: String): Flow<StatusContent> = statusRepository.status(id)

    open suspend fun post(
        message: String,
        key: String = UUID.randomUUID().toString(),
        language: String? = null,
        visibility: Visibility? = null
    ): Status = if(isAuthenticated) {
        dataSource.post(
            body = PostStatus(message, language, visibility),
            Endpoint.StatusEndpoint(key)
        ).apply {
            statusRepository.add(listOf(this))
        }
    } else {
        throw MissingTokenException
    }

    open suspend fun schedule(
        message: String,
        date: Date,
        key: String = UUID.randomUUID().toString(),
        language: String? = null,
        visibility: Visibility? = null
    ): ScheduledStatus = dataSource.post(
        body = ScheduleRequest(message, date, language, visibility),
        Endpoint.ScheduleStatus(key)
    )
}