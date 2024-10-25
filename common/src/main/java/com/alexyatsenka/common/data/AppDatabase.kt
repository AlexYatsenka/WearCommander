package com.alexyatsenka.common.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexyatsenka.common.data.models.CommandDB

@Database(
    entities = [
        CommandDB::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commandDao() : CommandDao
}
