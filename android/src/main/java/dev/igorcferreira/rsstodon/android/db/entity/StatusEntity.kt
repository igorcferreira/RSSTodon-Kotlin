package dev.igorcferreira.rsstodon.android.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "status")
data class StatusEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "uri") val uri: String
)