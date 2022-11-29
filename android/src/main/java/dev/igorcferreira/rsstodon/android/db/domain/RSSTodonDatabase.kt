package dev.igorcferreira.rsstodon.android.db.domain

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.igorcferreira.rsstodon.android.db.dao.StatusDao
import dev.igorcferreira.rsstodon.android.db.entity.StatusEntity
import dev.igorcferreira.rsstodon.android.db.mapper.DateConverter

@Database(entities = [StatusEntity::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class RSSTodonDatabase: RoomDatabase() {
    abstract fun statusDao(): StatusDao
}