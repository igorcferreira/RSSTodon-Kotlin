package dev.igorcferreira.rsstodon.api.repository

import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.api.model.response.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*

open class InMemoryStatusRepository(
    private val buffer: MutableStateFlow<List<Status>> = MutableStateFlow(emptyList())
): StatusRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun lastStatus(): Flow<StatusContent> = buffer.mapLatest { list ->
        list.first()
    }.map { it.asStatusContent(::formatContent) }

    override fun filterStatus(start: Date, limit: Int): Flow<List<StatusContent>> = buffer.map { list ->
        list.filter { it.createdAt.before(start) }
            .map { it.asStatusContent(::formatContent) }
    }

    override fun statusList(): Flow<List<StatusContent>> = buffer.map { list ->
        list.map { it.asStatusContent(::formatContent) }
    }

    override fun status(id: String): Flow<StatusContent> = flowOf(buffer.value.first {
        it.id.value == id
    }.asStatusContent(::formatContent))

    override fun add(statuses: List<Status>) {
        val values = buffer.value.toMutableList()
        val newItems = statuses.filter { item ->
            values.indexOfFirst { it.id == item.id } < 0
        }
        values.addAll(0, newItems)
        buffer.value = values.toList()
    }

    private fun Status.asStatusContent(contentFormatter: (Status) -> String) = this.let {
        StatusContent(
            it.id.value,
            "${it.account["display_name"]}".removeSurrounding("\""),
            it.uri.toString(),
            contentFormatter(it)
        )
    }

    open fun formatContent(status: Status): String = status.content
}