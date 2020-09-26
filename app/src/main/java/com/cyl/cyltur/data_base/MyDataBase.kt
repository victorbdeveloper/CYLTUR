package com.cyl.cyltur.data_base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cyl.cyltur.model.Monumento


@Database(entities = [Monumento::class], version = 1)
@TypeConverters(CustomConverters::class)
abstract class MyDatabase : RoomDatabase() {

    abstract fun getMonumentDao(): MonumentDao

}

