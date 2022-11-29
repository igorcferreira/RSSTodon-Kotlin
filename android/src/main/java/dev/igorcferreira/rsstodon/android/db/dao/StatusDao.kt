package dev.igorcferreira.rsstodon.android.db.dao

import androidx.room.*
import dev.igorcferreira.rsstodon.android.db.entity.StatusEntity
import dev.igorcferreira.rsstodon.api.model.response.Status
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface StatusDao {
    @Query("SELECT * FROM status ORDER BY created_at DESC")
    fun getAll(): Flow<List<StatusEntity>>

    @Query("SELECT * FROM status ORDER BY created_at DESC LIMIT 1")
    fun getLastItem(): Flow<StatusEntity>

    @Query("SELECT * FROM status WHERE created_at < :startingAt ORDER BY created_at DESC LIMIT :limit")
    fun getPage(startingAt: Date, limit: Int): Flow<List<StatusEntity>>

    @Query("SELECT * FROM status WHERE id = :id")
    fun get(id: String): Flow<StatusEntity>

    fun insert(status: List<Status>) = status.map {
        it.asStatusEntity()
    }.let { insertAll(it) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(statusEntities: List<StatusEntity>)

    private fun Status.asStatusEntity(): StatusEntity = this.let {
        StatusEntity(
            it.id.value,
            it.content,
            it.createdAt,
            "${it.account["display_name"]}".removeSurrounding("\""),
            it.uri.toString()
        )
    }
}