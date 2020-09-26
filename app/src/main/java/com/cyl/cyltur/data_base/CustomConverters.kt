package com.cyl.cyltur.data_base

import androidx.room.TypeConverter
import java.util.*

class CustomConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = if (value == null) null else Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromString(value: String?): List<String> = value?.split(",") ?: emptyList()

    @TypeConverter
    fun listToString(list: List<String>?): String? = list?.joinToString(",")

}
