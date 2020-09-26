package com.cyl.cyltur.data_base

import android.content.Context
import androidx.room.Room

class DbUtils {

    companion object {

        private var dataBase: MyDatabase? = null

        fun getDataBase(context: Context): MyDatabase {

            if (dataBase == null) {

                dataBase = Room.databaseBuilder(context, MyDatabase::class.java, "my-db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()

            }

            return dataBase!!

        }

    }

}