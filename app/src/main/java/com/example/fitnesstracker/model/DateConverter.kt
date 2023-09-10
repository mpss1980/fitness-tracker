package com.example.fitnesstracker.model

import androidx.room.TypeConverter
import java.util.Date

object DateConverter {

    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}