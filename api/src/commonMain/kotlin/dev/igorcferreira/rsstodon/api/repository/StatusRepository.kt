package dev.igorcferreira.rsstodon.api.repository

import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.api.model.response.Status
import kotlinx.coroutines.flow.Flow
import java.util.*

interface StatusRepository {
    fun lastStatus(): Flow<StatusContent>
    fun filterStatus(start: Date, limit: Int): Flow<List<StatusContent>>
    fun statusList(): Flow<List<StatusContent>>
    fun status(id: String): Flow<StatusContent>
    fun add(statuses: List<Status>)
}