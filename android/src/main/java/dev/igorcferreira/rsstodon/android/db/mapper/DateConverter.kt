package dev.igorcferreira.rsstodon.android.db.mapper

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromTimestamp(time: Long?): Date? = time?.let {
        Date(it)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time
}