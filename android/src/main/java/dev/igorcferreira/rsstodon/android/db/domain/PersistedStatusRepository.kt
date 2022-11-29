package dev.igorcferreira.rsstodon.android.db.domain

import android.content.Context
import androidx.room.Room
import dev.igorcferreira.rsstodon.android.db.entity.StatusEntity
import dev.igorcferreira.rsstodon.api.model.presentation.StatusContent
import dev.igorcferreira.rsstodon.api.model.response.Status
import dev.igorcferreira.rsstodon.api.repository.StatusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class PersistedStatusRepository(
    private val database: RSSTodonDatabase
): StatusRepository {

    constructor(
        context: Context
    ): this(getInstance(context))

    override fun lastStatus(): Flow<StatusContent> = database.statusDao()
        .getLastItem()
        .map { it.asStatusContent() }

    override fun filterStatus(start: Date, limit: Int): Flow<List<StatusContent>> = database.statusDao()
        .getPage(start, limit)
        .map { list -> list.map { it.asStatusContent() } }

    override fun statusList(): Flow<List<StatusContent>> = database
        .statusDao()
        .getAll()
        .map { list -> list.map { it.asStatusContent() } }

    override fun status(id: String): Flow<StatusContent> = database
        .statusDao()
        .get(id)
        .map { it.asStatusContent() }

    override fun add(statuses: List<Status>) {
        database.statusDao().insert(statuses)
    }

    private fun StatusEntity.asStatusContent() = this.let {
        StatusContent(it.id, it.displayName, it.uri, it.content)
    }

    companion object {
        lateinit var instance: RSSTodonDatabase

        fun getInstance(context: Context): RSSTodonDatabase {
            if (::instance.isInitialized) return instance
            synchronized(PersistedStatusRepository::class) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    RSSTodonDatabase::class.java,
                    "rsstodon-room-db"
                ).build()
            }
            return instance
        }
    }
}